package de.kochnetonline.sbkastp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.kochnetonline.sbkastp.model.DeliveryState;
import de.kochnetonline.sbkastp.model.SubscritionState;
import de.kochnetonline.sbkastp.service.EventProducerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class RestService {

    @Autowired
    private EventProducerService eventProducerService;

    @PutMapping("/subscribe/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "1_Subscription", description = "Subscription Endpoints")
    public void subscribe(@PathVariable("customerId") final Integer customerId) throws ExecutionException, JsonProcessingException, InterruptedException {
        eventProducerService.sendSubscriptionEvent(customerId, SubscritionState.ACTIVE, true);
        log.info("subscribed [{}]", customerId);

    }

    @PutMapping("/unsubscribe/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "1_Subscription", description = "Subscription Endpoints")
    public void unsubscribe(@PathVariable("customerId") final Integer customerId) throws ExecutionException, JsonProcessingException, InterruptedException {
        eventProducerService.sendSubscriptionEvent(customerId, SubscritionState.SUSPENDED, true);
        log.info("unsubscribed [{}]", customerId);
    }

    @PutMapping("/delivery/event/generate")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "2_Delivery", description = "Delivery Endpoints")
    public void generateDeliveryEvent(@RequestParam("customerId") final Integer customerId, @RequestParam("deliveryState") DeliveryState deliveryState, @RequestParam("parcelId") ExampleParcel exampleParcel) throws ExecutionException, InterruptedException, JsonProcessingException {
        eventProducerService.sendDeliveryEvent(customerId, exampleParcel.toString(), deliveryState, true);
        log.info("DeliveryEvent generated [{}]", customerId);
    }

    @PutMapping("/testdatagenerator/start")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "3_Testdatagenerator", description = "Testdatagenerator bulk load")
    public void testdatageneratorStart() {
        eventProducerService.startBulkLoad();
        log.info("Testdatagenerator bulk load");
    }

    @PutMapping("/testdatagenerator/stop")
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "3_Testdatagenerator", description = "Testdatagenerator bulk load")
    public void testdatageneratorStop() {
        eventProducerService.stopBulkLoad();
        log.info("Testdatagenerator bulk load");
    }

    private enum ExampleParcel {
        PARCEL_1, PARCEL_2, PARCEL_3
    }

}
