package de.kochnetonline.sbkastp.serde;

import de.kochnetonline.sbkastp.model.CustomerKey;
import de.kochnetonline.sbkastp.model.Subscription;
import de.kochnetonline.sbkastp.model.Win;
import de.kochnetonline.sbkastp.model.WinNotification;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class CustomJsonSerdes {

    public static Serde<CustomerKey> CustomerKey() {
        JsonSerializer<CustomerKey> serializer = new JsonSerializer<>();
        JsonDeserializer<CustomerKey> deserializer = new JsonDeserializer<>(CustomerKey.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Subscription> Subscription() {
        JsonSerializer<Subscription> serializer = new JsonSerializer<>();
        JsonDeserializer<Subscription> deserializer = new JsonDeserializer<>(Subscription.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Win> Win() {
        JsonSerializer<Win> serializer = new JsonSerializer<>();
        JsonDeserializer<Win> deserializer = new JsonDeserializer<>(Win.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<WinNotification> WinNotification() {
        JsonSerializer<WinNotification> serializer = new JsonSerializer<>();
        JsonDeserializer<WinNotification> deserializer = new JsonDeserializer<>(WinNotification.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }


}
