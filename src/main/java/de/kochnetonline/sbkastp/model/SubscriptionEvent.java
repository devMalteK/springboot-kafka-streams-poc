package de.kochnetonline.sbkastp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEvent implements Serializable {
    private Integer customerId;
    private String createTimestamp;
    private String emailAdress;
    private SubscritionState subscritionState;
}

