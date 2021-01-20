package com.joon.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joon.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest  //Web관련된 테스트만 수행하므로 SpringBootTest에 비해 빠름 MockMvc를 사용할 수 있게 해줌
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적인 입력 테스트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)  //입력 기대 형태
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsBytes(eventDto)))  //출력 기대형태
                .andDo(print())
                .andExpect(status().isCreated())//201 테스트
                .andExpect(jsonPath("id").exists()) // json에 id 가 있는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION))   //header에 LOCATION이 있는지 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)) // CONTENT_TYPE에 HAL JSON인지 확인
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }
    @Test
    @TestDescription("Event 타입으로 입력될 경우 실패 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)  //입력 기대 형태
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(event)))  //출력 기대형태
                .andDo(print())
                .andExpect(status().isBadRequest())//201 테스트

        ;
    }
    @Test
    @TestDescription("빈 객체 입력시 에러 발생 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto=EventDto.builder().build();
        mvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }
    @Test
    @TestDescription("입력 값이 잘못된 경우 에러 발생 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto=EventDto.builder()
                .name("Spring")
                .description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();
        mvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }
}


