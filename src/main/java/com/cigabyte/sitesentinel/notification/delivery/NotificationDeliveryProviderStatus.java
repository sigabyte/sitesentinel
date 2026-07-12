package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;

public class NotificationDeliveryProviderStatus {

    private final NotificationDeliveryChannel channel;

    private final boolean enabled;

    private final boolean credentialConfigured;

    private final boolean destinationConfigured;

    private final boolean endpointConfigured;

    private final boolean configurationComplete;

    private final boolean ready;

    private final NotificationDeliveryProviderReadinessStatus readinessStatus;

    private final NotificationDeliveryMode deliveryMode;

    private final String statusMessage;

    public NotificationDeliveryProviderStatus(
            NotificationDeliveryChannel channel,
            boolean enabled,
            boolean credentialConfigured,
            boolean destinationConfigured,
            boolean endpointConfigured,
            boolean configurationComplete,
            boolean ready,
            NotificationDeliveryProviderReadinessStatus readinessStatus,
            NotificationDeliveryMode deliveryMode,
            String statusMessage
    ) {
        this.channel = channel;
        this.enabled = enabled;
        this.credentialConfigured = credentialConfigured;
        this.destinationConfigured = destinationConfigured;
        this.endpointConfigured = endpointConfigured;
        this.configurationComplete = configurationComplete;
        this.ready = ready;
        this.readinessStatus = readinessStatus;
        this.deliveryMode = deliveryMode;
        this.statusMessage = statusMessage;
    }

    public NotificationDeliveryChannel getChannel() {
        return channel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isCredentialConfigured() {
        return credentialConfigured;
    }

    public boolean isDestinationConfigured() {
        return destinationConfigured;
    }

    public boolean isEndpointConfigured() {
        return endpointConfigured;
    }

    public boolean isConfigurationComplete() {
        return configurationComplete;
    }

    public boolean isReady() {
        return ready;
    }

    public NotificationDeliveryProviderReadinessStatus getReadinessStatus() {
        return readinessStatus;
    }

    public NotificationDeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}