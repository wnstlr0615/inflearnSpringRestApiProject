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

