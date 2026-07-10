package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import com.cigabyte.sitesentinel.notification.NotificationEvent;

public interface NotificationDeliveryProvider {

    NotificationDeliveryChannel getChannel();

    NotificationDeliveryProviderResult deliver(NotificationEvent notificationEvent);

    default boolean supports(NotificationDeliveryChannel channel) {
        return getChannel() == channel;
    }
}