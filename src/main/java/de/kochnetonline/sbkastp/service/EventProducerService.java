package de.kochnetonline.sbkastp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kochnetonline.sbkastp.config.AppConfig;
import de.kochnetonline.sbkastp.config.KafkaProducerConfig;
import de.kochnetonline.sbkastp.model.CustomerKey;
import de.kochnetonline.sbkastp.model.Subscription;
import de.kochnetonline.sbkastp.model.SubscritionState;
import de.kochnetonline.sbkastp.model.Win;
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
import java.util.concurrent.TimeoutException;

/**
 * produces a random Message every second
 */
@Service
@Slf4j
public class EventProducerService {
    private KafkaProducerConfig kafkaProducerConfig;
    private AppConfig appConfig;

    private CompletableFuture<Void> bulkLoadTask;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventProducerService(KafkaProducerConfig kafkaProducerConfig, AppConfig appConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        this.appConfig = appConfig;
    }
    /*
     *//**
     * Topic 1 with low Record Count
     * 1000 Records every 15 seconds
     *
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JsonProcessingException
     * @throws TimeoutException
     *//*

    public void produceMessageForTopic1() throws ExecutionException, InterruptedException, JsonProcessingException, TimeoutException {
        for (int i = 0; i < 1000; i++) {
            var producerRecord = generateProducerRecord(appConfig.getSourceTopicName1());
            kafkaProducerConfig.kafkaTemplate().send(producerRecord);

            log.debug("Message sent topic: [{}] key: [{}] value: [{}]", producerRecord.topic(), producerRecord.key(), producerRecord.value());
        }
        kafkaProducerConfig.kafkaTemplate().flush();
    }

    */

    /**
     * Topic 1 with low Record Count
     * 1000 Records every 5 seconds
     *
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JsonProcessingException
     * @throws TimeoutException
     *//*

    public void produceMessageForTopic2() throws ExecutionException, InterruptedException, JsonProcessingException, TimeoutException {
        for (int i = 0; i < 10000; i++) {
            var producerRecord = generateProducerRecord(appConfig.getSourceTopicName2());
            kafkaProducerConfig.kafkaTemplate().send(producerRecord);

            log.debug("Message sent topic: [{}] key: [{}] value: [{}]", producerRecord.topic(), producerRecord.key(), producerRecord.value());
        }
        kafkaProducerConfig.kafkaTemplate().flush();
    }

    private ProducerRecord<String, String> generateProducerRecord(String topic) throws JsonProcessingException {
        var messageKey = new CustomerKey(new Random().nextInt(10000));
        var message = new MyKafkaMessage(LocalDateTime.now().toString(), UUID.randomUUID().toString());
        var messageKeyString = objectMapper.writeValueAsString(messageKey);
        var messageString = objectMapper.writeValueAsString(message);
        var producerRecord = new ProducerRecord<>(topic, messageKeyString, messageString);
        return producerRecord;
    }*/
    public void sendSubscriptionEvent(Integer customerId, SubscritionState subscritionState, boolean flushDirectly) throws JsonProcessingException, ExecutionException, InterruptedException {
        var producerRecord = generateSubscrptionEventRecord(appConfig.getSubscriptionTopic(), customerId, subscritionState);
        kafkaProducerConfig.kafkaTemplate().send(producerRecord);
        if (flushDirectly) {
            kafkaProducerConfig.kafkaTemplate().flush();
        }
    }

    private ProducerRecord<String, String> generateSubscrptionEventRecord(String topic, Integer customerId, SubscritionState subscritionState) throws JsonProcessingException {
        var messageKey = new CustomerKey(customerId);
        var message = new Subscription(customerId, LocalDateTime.now().toString(), subscritionState);
        var messageKeyString = objectMapper.writeValueAsString(messageKey);
        var messageString = objectMapper.writeValueAsString(message);
        return new ProducerRecord<>(topic, messageKeyString, messageString);
    }

    public void sendWinEvent(Integer customerId, boolean flushDirectly) throws ExecutionException, InterruptedException, JsonProcessingException {
        var producerRecord = generateWinEventRecord(appConfig.getWinTopic(), customerId);
        kafkaProducerConfig.kafkaTemplate().send(producerRecord);
        if (flushDirectly) {
            kafkaProducerConfig.kafkaTemplate().flush();
        }
    }

    private ProducerRecord<String, String> generateWinEventRecord(String topic, Integer customerId) throws JsonProcessingException {
        var messageKey = new CustomerKey(customerId);
        var message = new Win(customerId, LocalDateTime.now().toString(), UUID.randomUUID().toString());
        var messageKeyString = objectMapper.writeValueAsString(messageKey);
        var messageString = objectMapper.writeValueAsString(message);
        return new ProducerRecord<>(topic, messageKeyString, messageString);
    }

    public void startBulkLoad() {
        if (this.bulkLoadTask == null || this.bulkLoadTask.isCancelled()) {
            this.bulkLoadTask = CompletableFuture.runAsync(() -> {

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

                //generate 100000 Win Events
                for (int i = 0; i < 100000; i++) {
                    try {
                        this.sendWinEvent(new Random().nextInt(100000), false);
                    } catch (Exception e) {
                        log.error("Error sending Win Event", e);
                    }
                }

                try {
                    kafkaProducerConfig.kafkaTemplate().flush();
                } catch (Exception e) {
                    log.error("Error flushing Win Event", e);
                }

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                while (true) {

                    //generate every 10 seconds a subscription change event
                    if (stopWatch.getTotalTimeSeconds() % 10 == 0) {
                        SubscritionState subscritionState = new Random().nextBoolean() ? SubscritionState.ACTIVE : SubscritionState.SUSPENDED;
                        try {
                            this.sendSubscriptionEvent(new Random().nextInt(1001, 2000), subscritionState, false);
                        } catch (Exception e) {
                            log.error("Error sending Subscription Event", e);
                        }
                    }

                    //generate every 1 second a new win event
                    if (stopWatch.getTotalTimeMillis() % 1000 == 0) {
                        try {
                            this.sendWinEvent(new Random().nextInt(100000), false);
                        } catch (Exception e) {
                            log.error("Error sending Subscription Event", e);
                        }

                        try {
                            kafkaProducerConfig.kafkaTemplate().flush();
                        } catch (Exception e) {
                            log.error("Error flushing Win Event", e);
                        }
                    }

                    //generate every 10 second for the subscribed customers
                    if (stopWatch.getTotalTimeSeconds() % 10 == 0) {
                        try {
                            this.sendWinEvent(new Random().nextInt(1000), false);
                        } catch (Exception e) {
                            log.error("Error sending Subscription Event", e);
                        }

                        try {
                            kafkaProducerConfig.kafkaTemplate().flush();
                        } catch (Exception e) {
                            log.error("Error flushing Win Event", e);
                        }
                    }
                }
            });
        }
    }

    public void stopBulkLoad() {
        this.bulkLoadTask.cancel(true);
    }
}
