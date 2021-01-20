package com.joon.demoinflearnrestapi.events;

import lombok.*;

import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of="id") //값 비교시 id hash 값 사용
// @Data 사용시 @EqualsAndHashCode 에서 모든 프로퍼티를 자동 사용하므로 문제 생길 수도 있음
public class Event {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int asePrice;
    private boolean offline;
    private boolean free;
    private EventStatus eventStatus;


}
