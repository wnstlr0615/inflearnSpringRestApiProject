package com.joon.demoinflearnrestapi.events;

import com.joon.demoinflearnrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler assembler){
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel pagedModel = assembler.toModel(page, e->new EventResource((Event) e));
        pagedModel.add(new Link("/docs/index.html#resoureces-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedModel);
    }
    @GetMapping("{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()){return ResponseEntity.notFound().build();}
        EventResource eventResource = new EventResource(optionalEvent.get());
        eventResource.add(new Link("/docs/index.html#resoureces-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }
    @PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors){
        Optional<Event> findEvent = eventRepository.findById(id);
        if(findEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if(errors.hasErrors()){return badRequest(errors);}
        eventValidator.validate(eventDto,errors);
        if(errors.hasErrors()){return badRequest(errors);}
        Event existingEvent = findEvent.get();
        modelMapper.map(eventDto, existingEvent);
        Event savedEvent=eventRepository.save(existingEvent);

        EventResource eventResource=new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resoureces-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if(errors.hasErrors()){//어노테이션 validation 검사
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);  //내부 로직 검사

        if(errors.hasErrors()){
            return badRequest(errors);
        }
        Event event=modelMapper.map(eventDto,Event.class);
        event.update();
        Event newEvent=eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
       // eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        return ResponseEntity.created(createUri).body(eventResource);
    }
    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
