package com.joon.demoinflearnrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String name;    //이벤트 이름
    private String description;     //이벤트 설명
    private LocalDateTime beginEnrollmentDateTime; //시작일
    private LocalDateTime closeEnrollmentDateTime; // 종료일
    private LocalDateTime beginEventDateTime; //이벤트 시작일
    private LocalDateTime endEventDateTime;  //이벤트 종료일
    private String location; // 모임 장소 없을 경우 온라인
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment; //인원제한

}
