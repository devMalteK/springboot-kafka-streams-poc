package de.kochnetonline.sbkastp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDeliveryEvent implements Serializable {
    private Integer customerId;
    private String parcelId;
    private String createTimestamp;
    private DeliveryState deliveryState;
}
