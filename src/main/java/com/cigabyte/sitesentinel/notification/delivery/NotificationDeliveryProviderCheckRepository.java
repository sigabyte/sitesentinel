package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationDeliveryProviderCheckRepository
        extends JpaRepository<NotificationDeliveryProviderCheck, UUID> {

    Optional<NotificationDeliveryProviderCheck>
    findFirstByChannelOrderByCheckedAtDesc(
            NotificationDeliveryChannel channel
    );

    List<NotificationDeliveryProviderCheck>
    findTop10ByChannelOrderByCheckedAtDesc(
            NotificationDeliveryChannel channel
    );
}