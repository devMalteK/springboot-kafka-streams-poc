package de.kochnetonline.sbkastp.config;

import de.kochnetonline.sbkastp.model.*;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

/**
 * Native Images Hints for JacksonMapped Classes
 */
@Configuration
@RegisterReflectionForBinding({CustomerKey.class, SubscriptionEvent.class, SubscritionState.class, ParcelDeliveryEvent.class, NotificationEvent.class, DeliveryState.class})
public class NativeImageHints {
}
