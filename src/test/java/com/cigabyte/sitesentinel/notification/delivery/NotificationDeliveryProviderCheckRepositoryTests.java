package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class NotificationDeliveryProviderCheckRepositoryTests {

    private static final OffsetDateTime FUTURE_BASE_TIME =
            OffsetDateTime.of(
                    2500,
                    1,
                    1,
                    12,
                    0,
                    0,
                    0,
                    ZoneOffset.UTC
            );

    private final NotificationDeliveryProviderCheckRepository repository;

    @Autowired
    NotificationDeliveryProviderCheckRepositoryTests(
            NotificationDeliveryProviderCheckRepository repository
    ) {
        this.repository = repository;
    }

    @Test
    void findLatestReturnsNewestCheckForRequestedChannel() {
        repository.saveAndFlush(
                providerCheck(
                        NotificationDeliveryChannel.SLACK,
                        NotificationDeliveryProviderCheckStatus.TIMEOUT,
                        FUTURE_BASE_TIME.plusMinutes(1),
                        "repository-ordering-slack-oldest",
                        null
                )
        );

        repository.saveAndFlush(
                providerCheck(
                        NotificationDeliveryChannel.SLACK,
                        NotificationDeliveryProviderCheckStatus.UNREACHABLE,
                        FUTURE_BASE_TIME.plusMinutes(2),
                        "repository-ordering-slack-middle",
                        null
                )
        );

        repository.saveAndFlush(
                providerCheck(
                        NotificationDeliveryChannel.SLACK,
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        FUTURE_BASE_TIME.plusMinutes(3),
                        "repository-ordering-slack-latest",
                        200
                )
        );

        repository.saveAndFlush(
                providerCheck(
                        NotificationDeliveryChannel.EMAIL,
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        FUTURE_BASE_TIME.plusYears(1),
                        "repository-ordering-other-channel",
                        200
                )
        );

        Optional<NotificationDeliveryProviderCheck> result =
                repository.findFirstByChannelOrderByCheckedAtDesc(
                        NotificationDeliveryChannel.SLACK
                );

        assertTrue(result.isPresent());

        NotificationDeliveryProviderCheck latestCheck =
                result.get();

        assertEquals(
                NotificationDeliveryChannel.SLACK,
                latestCheck.getChannel()
        );

        assertEquals(
                NotificationDeliveryProviderCheckStatus.HEALTHY,
                latestCheck.getStatus()
        );

        assertEquals(
                FUTURE_BASE_TIME.plusMinutes(3),
                latestCheck.getCheckedAt()
        );

        assertEquals(
                "repository-ordering-slack-latest",
                latestCheck.getResultMessage()
        );

        assertEquals(
                200,
                latestCheck.getHttpStatusCode()
        );
    }

    @Test
    void findRecentReturnsMaximumTenChecksInDescendingOrder() {
        List<NotificationDeliveryProviderCheck> checks =
                new ArrayList<>();

        for (int index = 0; index < 12; index++) {
            checks.add(
                    providerCheck(
                            NotificationDeliveryChannel.WEBHOOK,
                            NotificationDeliveryProviderCheckStatus.HEALTHY,
                            FUTURE_BASE_TIME.plusMinutes(index),
                            "repository-recent-check-" + index,
                            200
                    )
            );
        }

        repository.saveAllAndFlush(checks);

        List<NotificationDeliveryProviderCheck> result =
                repository.findTop10ByChannelOrderByCheckedAtDesc(
                        NotificationDeliveryChannel.WEBHOOK
                );

        assertEquals(
                10,
                result.size()
        );

        for (int resultIndex = 0;
             resultIndex < result.size();
             resultIndex++) {

            int expectedSourceIndex =
                    11 - resultIndex;

            NotificationDeliveryProviderCheck check =
                    result.get(resultIndex);

            assertEquals(
                    NotificationDeliveryChannel.WEBHOOK,
                    check.getChannel()
            );

            assertEquals(
                    "repository-recent-check-"
                            + expectedSourceIndex,
                    check.getResultMessage()
            );

            assertEquals(
                    FUTURE_BASE_TIME.plusMinutes(
                            expectedSourceIndex
                    ),
                    check.getCheckedAt()
            );
        }
    }

    @Test
    void findRecentDoesNotIncludeChecksFromAnotherChannel() {
        repository.saveAndFlush(
                providerCheck(
                        NotificationDeliveryChannel.IN_APP,
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        FUTURE_BASE_TIME.plusMinutes(1),
                        "repository-channel-filter-in-app",
                        200
                )
        );

        repository.saveAndFlush(
                providerCheck(
                        NotificationDeliveryChannel.WHATSAPP,
                        NotificationDeliveryProviderCheckStatus.FAILED,
                        FUTURE_BASE_TIME.plusYears(10),
                        "repository-channel-filter-whatsapp",
                        503
                )
        );

        List<NotificationDeliveryProviderCheck> result =
                repository.findTop10ByChannelOrderByCheckedAtDesc(
                        NotificationDeliveryChannel.IN_APP
                );

        assertFalse(result.isEmpty());

        assertTrue(
                result.stream().allMatch(
                        check ->
                                check.getChannel()
                                        == NotificationDeliveryChannel.IN_APP
                )
        );

        assertTrue(
                result.stream().noneMatch(
                        check ->
                                check.getResultMessage().equals(
                                        "repository-channel-filter-whatsapp"
                                )
                )
        );
    }

    private NotificationDeliveryProviderCheck providerCheck(
            NotificationDeliveryChannel channel,
            NotificationDeliveryProviderCheckStatus status,
            OffsetDateTime checkedAt,
            String resultMessage,
            Integer httpStatusCode
    ) {
        return new NotificationDeliveryProviderCheck(
                channel,
                status,
                checkedAt,
                resultMessage,
                httpStatusCode
        );
    }
}