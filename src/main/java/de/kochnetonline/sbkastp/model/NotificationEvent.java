package de.kochnetonline.sbkastp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {
    private Integer customerId;
    private String subscriptionTimestamp;
    private String emailAdress;
    private SubscritionState subscritionState;
    private String parcelId;
    private String deliveryTimestamp;
    private DeliveryState deliveryState;
}
