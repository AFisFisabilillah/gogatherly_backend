package com.gogatherly.gogatherly.scheduler;

import com.gogatherly.gogatherly.model.entity.Event;
import com.gogatherly.gogatherly.model.repository.EventRepository;
import com.gogatherly.gogatherly.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EventStatusUpdater {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Scheduled(timeUnit = TimeUnit.MINUTES , fixedDelay = 1 )
    public void updateStatusEventToactive(){
        log.info("Running schduled task : updateStatusEventToactive at {} ", LocalDateTime.now());
        List<Event> events = eventService.getEventStatusToActive();

        if(!events.isEmpty()){
            log.info("found {} events to active", events.size());
            for(Event event : events){
                eventService.updateStatusToActive(event);
                log.info("Event ID : {} -Title : '{}' Status changed to ACTIVE ", event.getId(), event.getTitle());
            }
        }else{
            log.info("no events found active");
        }
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1)
    public void updateStatusEventToCompleted(){
        log.info("Running schduled task : updateStatusEventToCompleted at {} ", LocalDateTime.now());
        List<Event> events = eventService.getEventStatusToCompleted();
        if(!events.isEmpty()){
            log.info("found {} events to COMPLETED", events.size());
            for(Event event : events){
                eventService.updateStatusToCompleted(event);
                log.info("Event ID : {} -Title : '{}' Status changed to COMPLETED ", event.getId(), event.getTitle());
            }
        }else{
            log.info("no events found COMPLETED");
        }
    }
}
