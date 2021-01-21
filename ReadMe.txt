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

---------------------------------------------------------------------------------------------
12. Event 생성 API 구현: 입력값 제한하기
EventDto 생성

DemoInflearnRestApiApplication 클래스
@Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
모델 메퍼를 사용하기 위해 빈으로 등록
EventController 클래스
modelMapper 라이브러리 사용
  Event event=modelMapper.map(eventDto,Event.class); //DTO를 클래스로 매칭 시켜준다.

슬라이스 테스트인 @WebMvcTest 에서 전체 테스트인 @SpringBootTest 로 변경
@SpringBootTest에 경우 MockMvc를 등록 안해주므로 @AutoConfigureMockMvc 어노테이션도 추가
목객체 제거
@WebMVcTest에 경우 가짜객체를 사용하여 디비를 사용하지 않지만
@SpringBootTest에 경우 디비를 사용하여 실제로 테스트 함

 .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
---------------------------------------------------------------------------------------------
13. Event 생성 API 구현: 입력값 이외에 에러 발생

application.properties 추가
spring.jackson.deserialization.fail-on-unknown-properties=true  // 객체 변환 시 객체 변수명 말고 다른 것이 있을 경우 에러 처리

 Bad_Request() 테스트 추가

---------------------------------------------------------------------------------------------
14. Event 생성 API 구현: Bad Request 처리하기
EventDto 생성을 통하여 잘못된 값 입력 방지(기존 Event 객체를 그대로 사용하여 필요 없는 값도 입력 될 수 있었음)
createEvent_Bad_Request_Empty_Input() // @Valid 를 사용하여  빈객체 입력시 에러 처리

EventDto 클래스
    @NotEmpty //String 타입
    @NotNull // 다른 타입
    @Min(0) //int 형 최소 값 0으로 설정

EventController 클래스
 public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors)  //DTO 클래스사용과 @Valid Errors 사용
    eventValidator.validate(eventDto, errors);  //내부 로직 검사 추가
 ※@Valid 에 경우 스프링 부트 2.3부터 제외 되어 새롭게 추가해 주어야 함
 <dependency>
             <groupId>javax.validation</groupId>
             <artifactId>validation-api</artifactId>
 </dependency>
 ※Errors 클래스에서 error을 검출 못하는 경우 validation 의존성 추가해 주어야 함
  <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-validation</artifactId>
 </dependency>


 EventValidator 클래스 를 등록 하여 내부 값 에러 검출
 @Component 등록 하여 빈으로 관리

   if(eventDto.getBasePrice()>eventDto.getMaxPrice() && eventDto.getMaxPrice()!=0){
             errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
             errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong.");
         }

EventControllerTests 클래스
public void createEvent_Bad_Request()  // 입력 DTO 를 제외한 다른 입력이 들어 오는 경우
public void createEvent_Bad_Request_Empty_Input() //빈 DTO 가 들어 오는 경우
public void createEvent_Bad_Request_Wrong_Input() //잘못된 값이 들어오는경우
테스트 3개 추가

 @TestDescription() 을 추가하여 테스트 설명

@TestDescription 어노테이션 생성
 @Target(ElementType.METHOD) //설정
 @Retention(RetentionPolicy.SOURCE) //유지시간
 public @interface TestDescription {
     String value();
 }
 ---------------------------------------------------------------------------------------------
15. Event 생성 API 구현: Bad Request 응답 본문 만들기

 @TestDescription("입력 값이 잘못된 경우 에러 발생 테스트")
    public void createEvent_Bad_Request_Wrong_Input(){}

   다음 메소드에서  잘못된 요청으로 인한 에러 발생 시 에러에 대한 응답 만들기  과정

.andExpect(jsonPath("$[0].objectName").exists())
.andExpect(jsonPath("$[0].defaultMessage").exists())
.andExpect(jsonPath("$[0].code").exists())
에러 응답에 다음과 같은 변수가 있는지 확인

EventController 클래스
  if(errors.hasErrors()){  //에러 발생시 에러 클래스를 body에 넣어서 반환
             return ResponseEntity.badRequest().body(errors);
 }

 errors 클래스는 자동으로 json으로 변환 해주지 않기 때문에 변환 클래스 생성해야 함

 @JsonComponent    //Json 관련 빈으로 관리
 public  class ErrorSerializer extends JsonSerializer<Errors>   // JsonSerializer 상속 받아 구현
 변환 과정 구현
  ---------------------------------------------------------------------------------------------

