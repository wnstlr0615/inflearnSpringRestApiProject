인프런 스프링 RestApi 백기선님 강의

##8. Event 생성 API 구현: Event 도메인 구현
롬복 사용
@Data 사용시 @EqualsAndHashCode 에서 모든 프로퍼티를 자동 사용하므로 문제 생길 수도 있음
테스트 클래스 AssertJ 사용

import static org.assertj.core.api.Assertions.assertThat;

AssertThat 메소드
isNotNull()  //Null이 아닌지 검사 메소드
isEqualTo()  // 같은지 비교
commit
-------------------------------------------------------------------------------------
##9. Event 생성 API 구현: 테스트 만들자
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
##10. Event 생성 API 구현: 201 응답 받기
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
EventController 클래스
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE) //produces 출력 타입 설정
     URI createUri = linkTo(EventController.class).slash("{id}").toUri(); //Hateoas 에서 제공 uri 생성
EventControllerTests 클래스

    ObjectMapper objectMapper; // 객체를 json 으로 변환 하는 클래스 사용
    objectMapper.writeValueAsBytes(event)// 변환

   .andExpect(jsonPath("id").exists()); // json에 id 가 있는지 확인

-------------------------------------------------------------------------------------
##11. Event 생성 API 구현: EventRepository 구현
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
##12. Event 생성 API 구현: 입력값 제한하기
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
##13. Event 생성 API 구현: 입력값 이외에 에러 발생

application.properties 추가
spring.jackson.deserialization.fail-on-unknown-properties=true  // 객체 변환 시 객체 변수명 말고 다른 것이 있을 경우 에러 처리

 Bad_Request() 테스트 추가

---------------------------------------------------------------------------------------------
##14. Event 생성 API 구현: Bad Request 처리하기
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
##15. Event 생성 API 구현: Bad Request 응답 본문 만들기

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
#16. Event 생성 API 구현: 비즈니스 로직 적용
Event 도메인에 update() 메소드 추가
EventTest 클래스에 testFree() ,testOffline() 테스트 메소드 추가

EventControllerTests 클래스
@TestDescription("정상적인 입력 테스트")
public void createEvent(){} 메소드에 offline, free 검사 코드 추가
  ---------------------------------------------------------------------------------------------
##17. Event 생성 API 구현: 매개변수를 이용한 테스트
    반복된 테스트 코드를 줄이기 위해서 JUnitParams 라이브러리 사용
    EventTests 클래스
    @RunWith(JUnitParamsRunner.class)  //어노테이션 추가

    @Parameters
    test() -> testFree(int basePrice, int maxPrice, boolean isFree)  로변경

    private  Object[] parametersForTestFree(){    //parametersFor 로 시작하면 Params에서 찾아서 입력
            return new Object[]{
                    new Object[]{0,0,true},
                    new Object[]{100,0,false},
                    new Object[]{0,100,false},
                    new Object[]{100,200,false}
            };
        }
        다음과 같은 메소드 생성 하여 testFree 메소드에 주입 또는
            @Parameters({
            "0,0,true",
            "100,0,false"
            })
            다음과 같은 방법 사용

    testOffline 메소드와 이와 같음
---------------------------------------------------------------------------------------------
##19. 스프링 HATEOAS 적용
ResourceSupport is now RepresentationModel
Resource is now EntityModel
Resources is now CollectionModel
PagedResources is now PagedModel

EventControllertests 클래스
createEvent()메소드
.andExpect(jsonPath("_links.self").exists())
.andExpect(jsonPath("_links.query-events").exists())
.andExpect(jsonPath("_links.update-event").exists())
Spring HATEOAS 를 통한 링크 검출 확인

클래스 생성 //HATEOAS 를 사용하기 위해 클래스 생성
class EventResource extends EntityModel<Event> { //1번 방법
    public EventResource(Event event, Link... links) {
        super(event, links);
    }
}

class EventResource extends RepresentationModel<Event> { //2번 방법
    @JsonUnwrapped // 객체를 풀어서 json로 바꿔줌
    Event event;
    public EventResource(Event event){
        this.event=event;
    }
    public Event getEvent() {
        return event;
    }
}
---------------------------------------------------------------------------------------------
##21. 스프링 REST Docs 적용
RestApi를 REST Docs를 이용하여 문서화 하기
테스트 클래스에
@AutoConfigureRestDocs 추가 후
mvc에 테스트 코드로 .andDo(document("create-event")) 입력 시 REST Docs으로 Docs생성

Target에 .adoc 형태에 문서 생성
내용에 응답이 한줄로 나오기 때문에 fommater를 이용하여 깔끔하게 변환 작업

@TestConfiguration
public class RestDocsConfiguration {
    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcBuilderCustomizer(){
        return configurer -> configurer.operationPreprocessors()
                    .withRequestDefaults(prettyPrint())  //requet 깔끔하게 정리
                    .withResponseDefaults(prettyPrint());  //response 깔끔하게 정리
    }
}

eventControllerTests에
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
추가
---------------------------------------------------------------------------------------------
##22. 스프링 REST Docs: 링크, (Req, Res) 필드와 헤더
(Req, Res) 필드와 헤더  문서 생성

.andDo(document("create-event",
                            links( //links.adoc 생성
                                    linkWithRel("self").description("link to self"),
                                    linkWithRel("query-events").description("link to query events"),
                                    linkWithRel("update-event").description("link to update an existing event")
                            ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        relaxedResponseFields( //request 값에 있는 중복 제거 중복이 있을 경우 에러
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new event"),
                                fieldWithPath("free").description("free of new event"),
                                fieldWithPath("offline").description("offline of new event"),
                                fieldWithPath("eventStatus").description("event status")
                        )
                        ))
---------------------------------------------------------------------------------------------
#24. PostgreSQL 적용 
main에는 PostgreSql을 사용하도록 설정
Test에는 H2를 사용하도록 설정

Test에 application설정 사용하는 방법
프로젝트 설정을 통해 Test디렉토리에 resource 폴더를 테스트 리소스 폴더로 지정
application-test.properties 생성 // application으로 설정 할 경우 main에도 덮어 씌워지기 때문에 -test를 붙여 설정
 application-test.properties 생성 후 사용테스트 클래스에  @ActiveProfiles("test") 붙여서 사용
 ---------------------------------------------------------------------------------------------
#25. 인덱스 핸들러 만들기 
기본 페이지로 index 페이지 생성( IndexController 클래스 생성)
잘못 입력하여 에러가 발생하였을경우 index로 돌아가는 link추가

잘못된 입력으로 인해서 에러를 반환할 때 에러만 반환해준 곳에 index로 돌아가는 링크도 추가 되도록 구현

 return ResponseEntity.badRequest().body(errors); ->badRequest(errors);
 
 메소드로 패키징
 private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
         return ResponseEntity.badRequest().body(new ErrorsResource(errors));
     }
     
  Error에 index 링크를 넣기 위해서 ErrorsResoure 클래스 생성
  
  public class ErrorsResource extends EntityModel<Errors> {
      public ErrorsResource(Errors content, Link... links){
          super(content, links);
          add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
      }
  }
  
  스프링부트 2.3부터  jackson 라이브러리가 Array를 만드는 것을 허용하지 않으므로
  
  ErrorSerializer 클래스에
jsonGenerator.writeFieldName("errors");추가
   
---------------------------------------------------------------------------------------------
#26. Event 목록 조회 API 
전체 Event 목록을 조회하는 API 생성

public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler assembler){ 
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel pagedModel = assembler.toModel(page, e->new EventResource((Event) e));
        pagedModel.add(new Link("/docs/index.html#resoureces-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedModel);
    }
    Pageable 클래스를 입력받아 페이징처리 파라미터로 page, size, sort 입력 받음
    PagedResourcesAssembler 클래스를 통해 현재 위치에서 기본 적으로 제공하는 다음, 이전 링크 등 자동 생성
    
 PagedModel pagedModel = assembler.toModel(page
                        , e->new EventResource((Event) e));
                       입력받은 event를 EventResourece고 감싸줘서 자신의 self 링크가 있도록 추가
  
   pagedModel.add(new Link("/docs/index.html#resoureces-events-list").withRel("profile"));
   doc 링크 추가
                       
@Test
@TestDescription("30개의 이벤트를 10개씩 두번쨰 페이지 조회하기")
public void queryEvents() {} 테스트 추가
---------------------------------------------------------------------------------------------
#27. Event 조회 API 
@Test
@TestDescription("기존의 이벤트를 하나 조회하기")
public void getEvent() 

@Test
@TestDescription("없는 이벤트는 조회했을 때 404 응답 받기")
public void getEvent404()

테스트 코드 추가

 @GetMapping("{id}")
public ResponseEntity getEvent(@PathVariable Integer id){}
 EventController 에 조회 API 추가
---------------------------------------------------------------------------------------------
#28. Events 수정 API 

event 수정 테스트 메소드 4개 추가
@PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, 
    @RequestBody @Valid EventDto eventDto, Errors errors)
    수정하기 api 추가
---------------------------------------------------------------------------------------------
#29. 테스트 코드 리팩토링 

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore
public class BaseControllerTest {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ModelMapper modelMapper;
}

BaseControllerTest를 만들어 상속함으로서 test 중복 속성제거
@Ignore를 붙이면 테스트에서 제외됨
---------------------------------------------------------------------------------------------
#30. Account 도메인 추가 
시큐리티 설정을 위한 Account 도메인 추가

public class Account {
    @Id
    @GeneratedValue
    private Integer id;
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER) //값을 여러 개 가질 수 있다
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
  @ElementCollection 여러 개의 값을 가질 경우 설정
  Event 도메인에 연관관계설정
  ---------------------------------------------------------------------------------------------
#31. 스프링 시큐리티 
UserDetaileService를 구현하는 AccountService 생성
loadUserByUsername 구현 

public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         Account account = accountRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }
    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream().map(r-> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet());
    }
    
AccountServicestest 클래스 
 public void findByUsername(){} 검증 테스트 구현
---------------------------------------------------------------------------------------------
#32. 예외 테스트 
 public void findByUserNameFail(){} 
 유저를 찾지 못햇을 경우 발생하는 에러테스트
 에러 잡는 3가지 방법(강의 참조) 
---------------------------------------------------------------------------------------------
#33. 스프링 시큐리티 기본 설정 
SecurityConfig 클래스를 생성하여 
public void configure(WebSecurity web) 를 구현하여 사용자 접근 설정
 @Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
        .passwordEncoder(passwordEncoder);
} //사용 설정
---------------------------------------------------------------------------------------------
#34. 스프링 시큐리티 폼 인증 설정 
public class AccountService implements UserDetailsService{
      @Autowired
        PasswordEncoder passwordEncoder;
        public Account saveAcount(Account account){
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            return accountRepository.save(account);
        }
}
PasswordEncoder를 이용하여 비밀번호 암호화 작업 및 테스트도 암호화로 테스트 

SecurityConfig{
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous()
                .and()
                .formLogin()
                .and()
                .authorizeRequests()
                    .mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
                    .anyRequest().authenticated();
    }
}
폼인증 추가 하여 Get 요청은 로그인사용자는 가능하도록 설정
---------------------------------------------------------------------------------------------


