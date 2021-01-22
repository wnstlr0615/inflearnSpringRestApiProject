package com.joon.demoinflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


class EventResource extends RepresentationModel<Event> {
    @JsonUnwrapped
    Event event;
    public EventResource(Event event){
        this.event=event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
    public Event getEvent() {
        return event;
    }
}
