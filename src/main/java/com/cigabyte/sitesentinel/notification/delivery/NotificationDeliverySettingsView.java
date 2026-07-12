package com.cigabyte.sitesentinel.notification.delivery;

import java.util.List;

public class NotificationDeliverySettingsView {

    private final NotificationDeliveryProviderStatus providerStatus;

    private final NotificationDeliveryProviderCheck latestCheck;

    private final List<NotificationDeliveryProviderCheck> recentChecks;

    public NotificationDeliverySettingsView(
            NotificationDeliveryProviderStatus providerStatus,
            NotificationDeliveryProviderCheck latestCheck,
            List<NotificationDeliveryProviderCheck> recentChecks
    ) {
        this.providerStatus = providerStatus;
        this.latestCheck = latestCheck;
        this.recentChecks = List.copyOf(recentChecks);
    }

    public NotificationDeliveryProviderStatus getProviderStatus() {
        return providerStatus;
    }

    public NotificationDeliveryProviderCheck getLatestCheck() {
        return latestCheck;
    }

    public List<NotificationDeliveryProviderCheck> getRecentChecks() {
        return recentChecks;
    }

    public boolean hasLatestCheck() {
        return latestCheck != null;
    }

    public boolean hasRecentChecks() {
        return !recentChecks.isEmpty();
    }
}