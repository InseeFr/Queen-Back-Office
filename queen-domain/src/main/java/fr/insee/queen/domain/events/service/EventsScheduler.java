package fr.insee.queen.domain.events.service;

import fr.insee.queen.domain.events.model.Event;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Conditional(value = EventsSchedulerCondition.class)
@Component
@AllArgsConstructor
public class EventsScheduler {
    private final EventsService eventsService;

    private static final Logger log = LoggerFactory.getLogger(EventsScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        List<Event> events = eventsService.getAllNewEvents();
        log.info("The time is now {}", dateFormat.format(new Date()));

        events.forEach(e -> {
            eventsService.ackEvent(e.getId());
            eventsService.publishEvent(e.getValue());
        });
    }
}
