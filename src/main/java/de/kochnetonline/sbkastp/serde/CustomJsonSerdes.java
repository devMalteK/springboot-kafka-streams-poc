package de.kochnetonline.sbkastp.serde;

import de.kochnetonline.sbkastp.model.CustomerKey;
import de.kochnetonline.sbkastp.model.NotificationEvent;
import de.kochnetonline.sbkastp.model.ParcelDeliveryEvent;
import de.kochnetonline.sbkastp.model.SubscriptionEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class CustomJsonSerdes {

    public static Serde<CustomerKey> CustomerKey() {
        JsonSerializer<CustomerKey> serializer = new JsonSerializer<>();
        JsonDeserializer<CustomerKey> deserializer = new JsonDeserializer<>(CustomerKey.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<SubscriptionEvent> Subscription() {
        JsonSerializer<SubscriptionEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<SubscriptionEvent> deserializer = new JsonDeserializer<>(SubscriptionEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ParcelDeliveryEvent> ParcelDelivery() {
        JsonSerializer<ParcelDeliveryEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<ParcelDeliveryEvent> deserializer = new JsonDeserializer<>(ParcelDeliveryEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<NotificationEvent> Notification() {
        JsonSerializer<NotificationEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<NotificationEvent> deserializer = new JsonDeserializer<>(NotificationEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
