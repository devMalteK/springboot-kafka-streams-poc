package de.kochnetonline.sbkastp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WinNotification implements Serializable {
    private Integer customerId;
    private String subscriptionTimestamp;
    private de.kochnetonline.sbkastp.model.SubscritionState subscritionState;
    private String winTimestamp;
    private String present;
}
