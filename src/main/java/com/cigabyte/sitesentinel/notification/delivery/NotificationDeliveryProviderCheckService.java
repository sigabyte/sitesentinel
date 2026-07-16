package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationDeliveryProviderCheckService {

    private final NotificationDeliveryProviderCheckRepository repository;

    public NotificationDeliveryProviderCheckService(
            NotificationDeliveryProviderCheckRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public NotificationDeliveryProviderCheck record(
            NotificationDeliveryChannel channel,
            NotificationDeliveryProviderCheckStatus status,
            String resultMessage
    ) {
        return record(
                channel,
                status,
                resultMessage,
                null
        );
    }

    @Transactional
    public NotificationDeliveryProviderCheck record(
            NotificationDeliveryChannel channel,
            NotificationDeliveryProviderCheckStatus status,
            String resultMessage,
            Integer httpStatusCode
    ) {
        NotificationDeliveryProviderCheck check =
                new NotificationDeliveryProviderCheck(
                        channel,
                        status,
                        OffsetDateTime.now(),
                        resultMessage,
                        httpStatusCode
                );

        return repository.save(check);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationDeliveryProviderCheck> findLatest(
            NotificationDeliveryChannel channel
    ) {
        return repository.findFirstByChannelOrderByCheckedAtDesc(channel);
    }

    @Transactional(readOnly = true)
    public List<NotificationDeliveryProviderCheck> findRecent(
            NotificationDeliveryChannel channel
    ) {
        return repository.findTop10ByChannelOrderByCheckedAtDesc(channel);
    }
}