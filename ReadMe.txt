인프런 스프링 RestApi 백기선님 강의

8. Event 생성 API 구현: Event 도메인 구현
롬복 사용
@Data 사용시 @EqualsAndHashCode 에서 모든 프로퍼티를 자동 사용하므로 문제 생길 수도 있음
테스트 클래스 AssertJ 사용

import static org.assertj.core.api.Assertions.assertThat;

AssertThat 메소드
isNotNull()  //Null이 아닌지 검사 메소드
isEqualTo()  // 같은지 비교
commit
-------------------------------------------------------------------------------------
9. Event 생성 API 구현: 테스트 만들자
컨트롤러 테스트
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest  //Web관련된 테스트만 수행하므로 SpringBootTest에 비해 빠름 MockMvc를 사용할 수 있게 해줌
mvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)  //입력 기대 형태
                        .accept(MediaTypes.HAL_JSON))  //출력 기대 형태
                    .andExpect(status().isCreated());//201 테스트

-------------------------------------------------------------------------------------
10. Event 생성 API 구현: 201 응답 받기
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
EventController 클래스
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE) //produces 출력 타입 설정
     URI createUri = linkTo(EventController.class).slash("{id}").toUri(); //Hateoas 에서 제공 uri 생성
EventControllerTests 클래스

    ObjectMapper objectMapper; // 객체를 json 으로 변환 하는 클래스 사용
    objectMapper.writeValueAsBytes(event)// 변환

   .andExpect(jsonPath("id").exists()); // json에 id 가 있는지 확인

-------------------------------------------------------------------------------------
11. Event 생성 API 구현: EventRepository 구현
public interface EventRepository  extends JpaRepository<Event, Integer>  //Repository 생성
   Event 클래스
    @Enumerated(EnumType.STRING) // 기본 값 EnumType.ORDINAL(숫자로 저장)
    private EventStatus eventStatus;

※@Enumerated 어노테이션은 JPA에서 enum 에 붙여 주어 하며 기본 값은  EnumType.ORDINAL  이기 때문에
※DB에 숫자로 저장됨 그렇기 때문에 EnumType.String 으로 변경해 주어야 한다.



EventController 클래스
    private final EventRepository eventRepository;
        public EventController(EventRepository eventRepository) {
            this.eventRepository = eventRepository;
        }
        다음과 같이 생성자를 통해 빈주입 @Autowride 는 권장 되지 않음
                Event newEvent = eventRepository.save(event);

EventControllerTest 클래스

@WebMvcTest 는  웹과 관련된 빈만 등록 하기 때문에 Repository는 불러오지 않기 때문에  @MockBean을 사용하여 가짜 객체 생성
    @MockBean
    EventRepository eventRepository;

    Mockito.when(eventRepository.save(event)).thenReturn(event); //save 메소드 실행시 반환 값 지정

 .andExpect(header().exists(HttpHeaders.LOCATION))   //header에 LOCATION이 있는지 확인
 .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)); // CONTENT_TYPE에 HAL JSON인지 확인
 HttpHeaders 클래스에 Header 값 상수로 저장되어 있음


