package de.kochnetonline.sbkastp.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kochnetonline.sbkastp.model.CustomerKey;
import de.kochnetonline.sbkastp.model.Subscription;
import de.kochnetonline.sbkastp.model.Win;

public class KafkaMapper {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private KafkaMapper() {
    }

    /**
     * maps a kafka message to a Win
     *
     * @param message
     * @return
     * @throws JsonProcessingException
     */
    public static Win mapToWin(String message) {
        try {
            return objectMapper.readValue(message, Win.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * maps a kafka message to a Subscription
     *
     * @param message
     * @return
     * @throws JsonProcessingException
     */
    public static Subscription mapToSubscription(String message) {
        try {
            return objectMapper.readValue(message, Subscription.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * maps a kafka message to a MyKafkaMessageKey
     *
     * @param message
     * @return
     * @throws JsonProcessingException
     */
    public static CustomerKey mapToMyKafkaMessageKey(String message) {
        try {
            return objectMapper.readValue(message, CustomerKey.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String mapToJSon(Object key) {
        try {
            return objectMapper.writeValueAsString(key);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
