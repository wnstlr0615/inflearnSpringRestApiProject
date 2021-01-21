package com.joon.demoinflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

class EventResource extends RepresentationModel<Event> {
    @JsonUnwrapped
    Event event;
    public EventResource(Event event){
        this.event=event;
    }
    public Event getEvent() {
        return event;
    }
}
