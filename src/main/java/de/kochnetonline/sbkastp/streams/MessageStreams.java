package de.kochnetonline.sbkastp.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kochnetonline.sbkastp.config.AppConfig;
import de.kochnetonline.sbkastp.config.KafkaConsumerConfig;
import de.kochnetonline.sbkastp.model.*;
import de.kochnetonline.sbkastp.serde.CustomJsonSerdes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * consumes messages from kafka and persists them
 */
@Component
@Slf4j
public class MessageStreams {

    private AppConfig appConfig;
    private KafkaConsumerConfig kafkaConsumerConfig;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MessageStreams(AppConfig appConfig, KafkaConsumerConfig kafkaConsumerConfig) {
        this.appConfig = appConfig;
        this.kafkaConsumerConfig = kafkaConsumerConfig;
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<CustomerKey, Subscription> subscriptionStream = streamsBuilder
                .stream(appConfig.getSubscriptionTopic(), Consumed.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.Subscription()))
                .peek((key, value) -> log.info("Subscription incoming record key [{}] value [{}]", key, value));


        KStream<CustomerKey, Win> winStream = streamsBuilder
                .stream(appConfig.getWinTopic(), Consumed.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.Win()))
                .peek((key, value) -> log.info("Win stream incoming record key [{}] value [{}]", key, value));


        KTable<CustomerKey, Subscription> tabelOfActiveSubscriptions = subscriptionStream
                .filter((key, value) -> value.getSubscritionState().equals(SubscritionState.ACTIVE))
                .toTable();

        winStream.join(tabelOfActiveSubscriptions, MessageStreams::mapToTargetTopicKeyValue)
                .peek((key, value) -> log.info("WinNotification generated record key [{}] value [{}]", key, value))
                .to(appConfig.getWinNotificationTopic(), Produced.with(CustomJsonSerdes.CustomerKey(), CustomJsonSerdes.WinNotification()));


        //final Topology topology = streamsBuilder.build();
        //streamsInnerJoin = new KafkaStreams(topology, props);
        //streamsInnerJoin.start();
    }

    private static WinNotification mapToTargetTopicKeyValue(CustomerKey key, Win win, Subscription subscription) {
        return new WinNotification(key.getCustomerId(), subscription.getCreateTimestamp(), subscription.getSubscritionState(), win.getCreateTimestamp(), win.getPresent());
    }


}
