package de.kochnetonline.sbkastp.processor;

import de.kochnetonline.sbkastp.config.AppConfig;
import de.kochnetonline.sbkastp.model.*;
import de.kochnetonline.sbkastp.serde.CustomJsonSerdes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * consumes messages from kafka and persists them
 */
@Component
@Slf4j
public class DeliveryEventProcessor {

    private final AppConfig appConfig;

    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    public DeliveryEventProcessor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Autowired
    void buildDeliveryEventProcessingPipeline(StreamsBuilder streamsBuilder) {
        //create Stream for Subscription Topic
        KStream<CustomerKey, SubscriptionEvent> subscriptionStream = streamsBuilder
                .stream(appConfig.getSubscriptionTopic(), Consumed.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.Subscription()))
                .peek((key, value) -> log.info("\t\tSubscription incoming record key [{}] value [{}]", key, value));

        //generate State for all "ACTIVE" subscriptions
        KTable<CustomerKey, SubscriptionEvent> tabelOfActiveSubscriptions = subscriptionStream
                .toTable()
                .filter((key, value) -> value.getSubscritionState().equals(SubscritionState.ACTIVE));

        //create Stream for Delivery Event Topic
        KStream<CustomerKey, ParcelDeliveryEvent> deliveryEventStream = streamsBuilder
                .stream(appConfig.getDeliveryTopic(), Consumed.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.ParcelDelivery()))
                .filter((key, value) ->
                        value.getDeliveryState().equals(DeliveryState.DELIVERED)
                )
                .peek((key, value) -> log.info("\tDeliveryEvent incoming record key [{}] value [{}]", key, value));

        //join SubscriptionStateTable with delivery Events and write a Notification in a new Topic
        deliveryEventStream.join(tabelOfActiveSubscriptions, DeliveryEventProcessor::mapToTargetTopicKeyValue)
                .peek((key, value) -> {
                    log.info("Processing record key [{}] value [{}]", key, value);
                })
                .to(appConfig.getNotificationTopic(), Produced.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.Notification()));

        //consume Notification Topic and generate Aggregation Count of delivered parcels for each customer
        streamsBuilder
                .stream(appConfig.getNotificationTopic(), Consumed.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.Notification()))
                .peek((key, value) -> {
                    log.info("Notification consumed record key [{}] value [{}]", key, value);
                    latch.countDown();
                })
                .groupByKey()
                .count(Materialized.as("CountDeliveredParcels"))
                .mapValues(value -> "delivered Parcels:" + value)
                .toStream()
                .to(appConfig.getNotificationCountTopic(), Produced.with(CustomJsonSerdes.CustomerKey(), Serdes.String()));
        ;
    }

    private static NotificationEvent mapToTargetTopicKeyValue(CustomerKey key, ParcelDeliveryEvent parcelDeliveryEvent, SubscriptionEvent subscriptionEvent) {
        return new NotificationEvent(key.getCustomerId(), subscriptionEvent.getCreateTimestamp(), subscriptionEvent.getEmailAdress(), subscriptionEvent.getSubscritionState(), parcelDeliveryEvent.getParcelId(), parcelDeliveryEvent.getCreateTimestamp(), parcelDeliveryEvent.getDeliveryState());
    }
}
