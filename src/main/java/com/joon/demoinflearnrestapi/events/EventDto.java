package com.joon.demoinflearnrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    @NotEmpty
    private String name;    //이벤트 이름
    @NotEmpty
    private String description;     //이벤트 설명
    @NotNull
    private LocalDateTime beginEnrollmentDateTime; //시작일
    @NotNull
    private LocalDateTime closeEnrollmentDateTime; // 종료일
    @NotNull
    private LocalDateTime beginEventDateTime; //이벤트 시작일
    @NotNull
    private LocalDateTime endEventDateTime;  //이벤트 종료일
    private String location; // 모임 장소 없을 경우 온라인
    @Min(0)
    private int basePrice;
    @Min(0)
    private int maxPrice;
    @Min(0)
    private int limitOfEnrollment; //인원제한

}
