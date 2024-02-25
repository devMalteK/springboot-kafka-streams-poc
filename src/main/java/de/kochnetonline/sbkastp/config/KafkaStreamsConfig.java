package de.kochnetonline.sbkastp.config;


import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.CleanupConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Configuration for Kafka Streams
 */
@Configuration
@EnableKafka
//@EnableKafkaStreams
public class KafkaStreamsConfig {

    //constructor-injection doesn't work - don't know why
    @Autowired
    private AppConfig appConfig;

    //constructor-injection doesn't work - don't know why
    @Autowired
    private MeterRegistry meterRegistry;

    @PostConstruct
    private void createTopics() throws ExecutionException, InterruptedException {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaBootstrapServer());
        AdminClient admin = AdminClient.create(config);
        ListTopicsResult listTopics = admin.listTopics();
        Set<String> names = listTopics.names().get();
        List<NewTopic> topicList = new ArrayList<NewTopic>();

        if (!names.contains(appConfig.getSubscriptionTopic())) {
            Map<String, String> configs = new HashMap<String, String>();
            int partitions = 5;
            Short replication = 1;
            NewTopic newTopic = new NewTopic(appConfig.getSubscriptionTopic(), partitions, replication).configs(configs);
            topicList.add(newTopic);
        }

        if (!names.contains(appConfig.getDeliveryTopic())) {
            Map<String, String> configs = new HashMap<String, String>();
            int partitions = 5;
            Short replication = 1;
            NewTopic newTopic = new NewTopic(appConfig.getDeliveryTopic(), partitions, replication).configs(configs);
            topicList.add(newTopic);
        }

        if (!names.contains(appConfig.getNotificationTopic())) {
            Map<String, String> configs = new HashMap<String, String>();
            int partitions = 1;
            Short replication = 1;
            NewTopic newTopic = new NewTopic(appConfig.getNotificationTopic(), partitions, replication).configs(configs);
            topicList.add(newTopic);
        }

        if (!names.contains(appConfig.getNotificationCountTopic())) {
            Map<String, String> configs = new HashMap<String, String>();
            int partitions = 1;
            Short replication = 1;
            NewTopic newTopic = new NewTopic(appConfig.getNotificationCountTopic(), partitions, replication).configs(configs);
            topicList.add(newTopic);
        }

        if (!topicList.isEmpty()) {
            admin.createTopics(topicList);
        }
    }

    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaBootstrapServer());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME)
    public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder() throws IOException {
        return new StreamsBuilderFactoryBean(kStreamsConfig(), new CleanupConfig(this.appConfig.getCleanUpStatesOnStartup(), true));
    }
}
