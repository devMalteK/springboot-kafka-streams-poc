package de.kochnetonline.sbkastp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kochnetonline.sbkastp.config.AppConfig;
import de.kochnetonline.sbkastp.config.KafkaProducerConfig;
import de.kochnetonline.sbkastp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * produces a random Message every second
 */
@Service
@Slf4j
public class EventProducerService {
    private final KafkaProducerConfig kafkaProducerConfig;
    private final AppConfig appConfig;

    private CompletableFuture<Void> bulkLoadTask;

    private AtomicBoolean bulkloadStopped = new AtomicBoolean(false);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventProducerService(KafkaProducerConfig kafkaProducerConfig, AppConfig appConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        this.appConfig = appConfig;
    }

    public void sendSubscriptionEvent(Integer customerId, SubscritionState subscritionState, boolean flushDirectly) throws JsonProcessingException, ExecutionException, InterruptedException {
        var producerRecord = generateSubscrptionEventRecord(appConfig.getSubscriptionTopic(), customerId, subscritionState);
        kafkaProducerConfig.kafkaTemplate().send(producerRecord);
        if (flushDirectly) {
            kafkaProducerConfig.kafkaTemplate().flush();
        }
    }

    private ProducerRecord<String, String> generateSubscrptionEventRecord(String topic, Integer customerId, SubscritionState subscritionState) throws JsonProcessingException {
        var messageKey = new CustomerKey(customerId);
        var message = new SubscriptionEvent(customerId, LocalDateTime.now().toString(), customerId + "@myemail.com", subscritionState);
        var messageKeyString = objectMapper.writeValueAsString(messageKey);
        var messageString = objectMapper.writeValueAsString(message);
        return new ProducerRecord<>(topic, messageKeyString, messageString);
    }

    public void sendDeliveryEvent(Integer customerId, String parcelId, DeliveryState deliveryState, boolean flushDirectly) throws ExecutionException, InterruptedException, JsonProcessingException {
        var producerRecord = generateDeliveryEventRecord(appConfig.getDeliveryTopic(), customerId, parcelId, deliveryState);
        kafkaProducerConfig.kafkaTemplate().send(producerRecord);
        if (flushDirectly) {
            kafkaProducerConfig.kafkaTemplate().flush();
        }
    }

    private ProducerRecord<String, String> generateDeliveryEventRecord(String topic, Integer customerId, String parcelId, DeliveryState deliveryState) throws JsonProcessingException {
        var messageKey = new CustomerKey(customerId);
        var message = new ParcelDeliveryEvent(customerId, parcelId, LocalDateTime.now().toString(), deliveryState);
        var messageKeyString = objectMapper.writeValueAsString(messageKey);
        var messageString = objectMapper.writeValueAsString(message);
        return new ProducerRecord<>(topic, messageKeyString, messageString);
    }

    public void startBulkLoad() {
        this.bulkloadStopped.set(false);

        if (this.bulkLoadTask == null || this.bulkLoadTask.isCancelled() || this.bulkLoadTask.isDone()) {
            this.bulkLoadTask = CompletableFuture.supplyAsync(() -> {

                //generate 1000 active subscribtions
                for (int i = 0; i < 1000; i++) {
                    try {
                        this.sendSubscriptionEvent(i, SubscritionState.ACTIVE, false);
                    } catch (Exception e) {
                        log.error("Error sending Subscription Event", e);
                    }
                }

                try {
                    kafkaProducerConfig.kafkaTemplate().flush();
                } catch (Exception e) {
                    log.error("Error flushing Subscription Event", e);
                }

                //generate 100000 Delivery Events
                for (int i = 0; i < 100000; i++) {
                    try {
                        this.sendDeliveryEvent(new Random().nextInt(100000), UUID.randomUUID().toString(), new Random().nextBoolean() ? DeliveryState.DELIVERED : DeliveryState.IN_DELIVERY, false);
                    } catch (Exception e) {
                        log.error("Error sending Delivery Event", e);
                    }
                }

                try {
                    kafkaProducerConfig.kafkaTemplate().flush();
                } catch (Exception e) {
                    log.error("Error flushing Delivery Event", e);
                }

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                while (bulkloadStopped.get()) {

                    //generate every 10 seconds a subscription change event
                    if (stopWatch.getTotalTimeSeconds() % 10 == 0) {
                        SubscritionState subscritionState = new Random().nextBoolean() ? SubscritionState.ACTIVE : SubscritionState.SUSPENDED;
                        try {
                            this.sendSubscriptionEvent(new Random().nextInt(1001, 2000), subscritionState, false);
                        } catch (Exception e) {
                            log.error("Error sending Subscription Event", e);
                        }
                    }

                    //generate every 1 second a new Delivery Event
                    if (stopWatch.getTotalTimeMillis() % 1000 == 0) {
                        try {
                            this.sendDeliveryEvent(new Random().nextInt(100000), UUID.randomUUID().toString(), new Random().nextBoolean() ? DeliveryState.DELIVERED : DeliveryState.IN_DELIVERY, false);
                        } catch (Exception e) {
                            log.error("Error sending Subscription Event", e);
                        }

                        try {
                            kafkaProducerConfig.kafkaTemplate().flush();
                        } catch (Exception e) {
                            log.error("Error flushing Delivery Event", e);
                        }
                    }

                    //generate every 10 second for the subscribed customers
                    if (stopWatch.getTotalTimeSeconds() % 10 == 0) {
                        try {
                            this.sendDeliveryEvent(new Random().nextInt(1000), UUID.randomUUID().toString(), DeliveryState.DELIVERED, false);
                        } catch (Exception e) {
                            log.error("Error sending Subscription Event", e);
                        }

                        try {
                            kafkaProducerConfig.kafkaTemplate().flush();
                        } catch (Exception e) {
                            log.error("Error flushing Delivery Event", e);
                        }
                    }
                }
                return null;
            });
        }
    }

    public void stopBulkLoad() {
        this.bulkloadStopped.set(true);
        this.bulkLoadTask.cancel(true);
    }
}
