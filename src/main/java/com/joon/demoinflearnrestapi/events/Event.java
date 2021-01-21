package com.joon.demoinflearnrestapi.events;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of="id") //값 비교시 id hash 값 사용
@Entity
// @Data 사용시 @EqualsAndHashCode 에서 모든 프로퍼티를 자동 사용하므로 문제 생길 수도 있음
public class Event extends RepresentationModel<Event> {
    @Id
    @GeneratedValue
    private Integer id;
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
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING) // 기본 값 EnumType.ORDINAL(숫자로 저장)
    private EventStatus eventStatus=EventStatus.DRAFT;

    public void update(){
        free= basePrice == 0 && maxPrice == 0;
        offline= !location.isBlank() && location != null;
    }

}
