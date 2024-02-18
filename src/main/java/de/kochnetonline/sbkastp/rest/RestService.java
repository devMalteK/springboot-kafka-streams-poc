package de.kochnetonline.sbkastp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.kochnetonline.sbkastp.model.SubscritionState;
import de.kochnetonline.sbkastp.service.EventProducerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class RestService {

    @Autowired
    private EventProducerService eventProducerService;

    @PutMapping("/subscribe/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "Subscription", description = "Subscription Endpoints")
    public void subscribe(@PathVariable("customerId") final Integer customerId) throws ExecutionException, JsonProcessingException, InterruptedException {
        eventProducerService.sendSubscriptionEvent(customerId, SubscritionState.ACTIVE, true);
        log.info("subscribed [{}]", customerId);

    }

    @PutMapping("/unsubscribe/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "Subscription", description = "Subscription Endpoints")
    public void unsubscribe(@PathVariable("customerId") final Integer customerId) throws ExecutionException, JsonProcessingException, InterruptedException {
        eventProducerService.sendSubscriptionEvent(customerId, SubscritionState.SUSPENDED, true);
        log.info("unsubscribed [{}]", customerId);
    }

    @PutMapping("/win/generate/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "Win", description = "Win Endpoints")
    public void generateWin(@PathVariable("customerId") final Integer customerId) throws ExecutionException, InterruptedException, JsonProcessingException {
        eventProducerService.sendWinEvent(customerId, true);
        log.info("win generated [{}]", customerId);
    }
}
