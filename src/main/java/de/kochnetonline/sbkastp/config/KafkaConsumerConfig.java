package de.kochnetonline.sbkastp.config;


import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ValidationException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Kafka Consumer
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    //constructor-injection doesn't work - don't know why
    @Autowired
    private AppConfig appConfig;

    //constructor-injection doesn't work - don't know why
    @Autowired
    private MeterRegistry meterRegistry;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() throws ValidationException {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, appConfig.getConsumerGroup());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //start from Beginning, when consumerGroup is new to the Topic or Consumer-group was reset
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //do not commit on error
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        final DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumerFactory.addListener(new MicrometerConsumerListener<>(this.meterRegistry));
        return consumerFactory;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        final ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
