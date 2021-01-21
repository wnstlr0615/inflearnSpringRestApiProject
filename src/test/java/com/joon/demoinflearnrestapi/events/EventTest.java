package com.joon.demoinflearnrestapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test
    public void builder(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        //Given
        String name = "Event";
        String description = "Spring";
        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree(){
        Event event=Event.builder()
                    .basePrice(0)
                    .maxPrice(0)
                    .build();

        event.update();
        assertThat(event.isFree()).isTrue();

        event=Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        event.update();
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline(){
        Event event=Event.builder()
                .location("강남역 네이버 D2 스타텁 팩토리")
                .build();

        event.update();

        assertThat(event.isOffline()).isTrue();


        event=Event.builder()
                    .location("")
                    .build();
        event.update();

        assertThat(event.isOffline()).isFalse();

    }
}