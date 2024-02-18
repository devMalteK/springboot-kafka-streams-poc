package de.kochnetonline.sbkastp.config;

import de.kochnetonline.sbkastp.model.*;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

/**
 * Native Images Hints for JacksonMapped Classes
 */
@Configuration
@RegisterReflectionForBinding({CustomerKey.class, Subscription.class, SubscritionState.class, Win.class, WinNotification.class})
public class NativeImageHints {
}
