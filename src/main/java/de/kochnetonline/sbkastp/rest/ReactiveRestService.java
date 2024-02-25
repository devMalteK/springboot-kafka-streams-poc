package de.kochnetonline.sbkastp.rest;

import de.kochnetonline.sbkastp.config.AppConfig;
import de.kochnetonline.sbkastp.config.KafkaConsumerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@Slf4j
public class ReactiveRestService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    @RequestMapping(value = "/getNotifications", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Tag(name = "4_Reactive Rest", description = "Experimental Reactive Rest Endpoints")
    @Operation(summary = "Note: request Endpoint in separate Browser Window/Tab, because swagger doesn't show the sent Events while the Stream is still open :-/")
    public Flux<String> getNotifications() {
        return kafkaConsumerConfig.reactiveKafkaConsumerTemplate(appConfig.getNotificationTopic(), UUID.randomUUID().toString())
                .receiveAutoAck()
                .doOnNext(consumerRecord -> log.debug("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                )
                .map(ConsumerRecord::value)
                .window(1)
                //.windowTimeout(20, Duration.ofSeconds(5, 0))
                .flatMap(window -> window.reduce((a, b) -> a));
    }

}
