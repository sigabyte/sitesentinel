package com.cigabyte.sitesentinel.notification.delivery;

import com.cigabyte.sitesentinel.notification.NotificationDeliveryChannel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationDeliveryProviderCheckServiceTests {

    @Test
    void recordWithoutHttpStatusSavesProviderCheckWithNullHttpStatus() {
        RecordingProviderCheckRepository repository =
                new RecordingProviderCheckRepository();

        NotificationDeliveryProviderCheckService service =
                new NotificationDeliveryProviderCheckService(
                        repository.asRepository()
                );

        NotificationDeliveryProviderCheck result =
                service.record(
                        NotificationDeliveryChannel.TELEGRAM,
                        NotificationDeliveryProviderCheckStatus.DISABLED,
                        "Telegram delivery provider is disabled."
                );

        assertEquals(1, repository.getSaveCallCount());
        assertSame(repository.getSavedCheck(), result);

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                result.getChannel()
        );

        assertEquals(
                NotificationDeliveryProviderCheckStatus.DISABLED,
                result.getStatus()
        );

        assertEquals(
                "Telegram delivery provider is disabled.",
                result.getResultMessage()
        );

        assertNotNull(result.getCheckedAt());
        assertNull(result.getHttpStatusCode());
    }

    @Test
    void recordWithHttpStatusSavesProviderCheckWithHttpStatus() {
        RecordingProviderCheckRepository repository =
                new RecordingProviderCheckRepository();

        NotificationDeliveryProviderCheckService service =
                new NotificationDeliveryProviderCheckService(
                        repository.asRepository()
                );

        NotificationDeliveryProviderCheck result =
                service.record(
                        NotificationDeliveryChannel.TELEGRAM,
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        "Telegram Bot API connectivity was verified.",
                        200
                );

        assertEquals(1, repository.getSaveCallCount());
        assertSame(repository.getSavedCheck(), result);

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                result.getChannel()
        );

        assertEquals(
                NotificationDeliveryProviderCheckStatus.HEALTHY,
                result.getStatus()
        );

        assertEquals(
                "Telegram Bot API connectivity was verified.",
                result.getResultMessage()
        );

        assertNotNull(result.getCheckedAt());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void recordRejectsInvalidHttpStatusBeforeRepositorySave() {
        RecordingProviderCheckRepository repository =
                new RecordingProviderCheckRepository();

        NotificationDeliveryProviderCheckService service =
                new NotificationDeliveryProviderCheckService(
                        repository.asRepository()
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.record(
                                NotificationDeliveryChannel.TELEGRAM,
                                NotificationDeliveryProviderCheckStatus.FAILED,
                                "Invalid HTTP status simulation.",
                                600
                        )
                );

        assertEquals(
                "HTTP status code must be between 100 and 599.",
                exception.getMessage()
        );

        assertEquals(0, repository.getSaveCallCount());
        assertNull(repository.getSavedCheck());
    }

    @Test
    void findLatestDelegatesChannelAndReturnsRepositoryResult() {
        RecordingProviderCheckRepository repository =
                new RecordingProviderCheckRepository();

        NotificationDeliveryProviderCheck expectedCheck =
                providerCheck(
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        200
                );

        repository.setLatestResult(
                Optional.of(expectedCheck)
        );

        NotificationDeliveryProviderCheckService service =
                new NotificationDeliveryProviderCheckService(
                        repository.asRepository()
                );

        Optional<NotificationDeliveryProviderCheck> result =
                service.findLatest(
                        NotificationDeliveryChannel.TELEGRAM
                );

        assertTrue(result.isPresent());
        assertSame(expectedCheck, result.get());

        assertEquals(1, repository.getFindLatestCallCount());

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                repository.getLatestRequestedChannel()
        );
    }

    @Test
    void findRecentDelegatesChannelAndReturnsRepositoryResult() {
        RecordingProviderCheckRepository repository =
                new RecordingProviderCheckRepository();

        NotificationDeliveryProviderCheck firstCheck =
                providerCheck(
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        200
                );

        NotificationDeliveryProviderCheck secondCheck =
                providerCheck(
                        NotificationDeliveryProviderCheckStatus.TIMEOUT,
                        null
                );

        repository.setRecentResult(
                List.of(
                        firstCheck,
                        secondCheck
                )
        );

        NotificationDeliveryProviderCheckService service =
                new NotificationDeliveryProviderCheckService(
                        repository.asRepository()
                );

        List<NotificationDeliveryProviderCheck> result =
                service.findRecent(
                        NotificationDeliveryChannel.TELEGRAM
                );

        assertEquals(2, result.size());
        assertSame(firstCheck, result.get(0));
        assertSame(secondCheck, result.get(1));

        assertEquals(1, repository.getFindRecentCallCount());

        assertEquals(
                NotificationDeliveryChannel.TELEGRAM,
                repository.getRecentRequestedChannel()
        );
    }

    @Test
    void findLatestReturnsEmptyWhenRepositoryHasNoCheck() {
        RecordingProviderCheckRepository repository =
                new RecordingProviderCheckRepository();

        NotificationDeliveryProviderCheckService service =
                new NotificationDeliveryProviderCheckService(
                        repository.asRepository()
                );

        Optional<NotificationDeliveryProviderCheck> result =
                service.findLatest(
                        NotificationDeliveryChannel.TELEGRAM
                );

        assertTrue(result.isEmpty());
        assertEquals(1, repository.getFindLatestCallCount());
    }

    @Test
    void providerCheckInitializesPersistenceTimestampsOnCreate() {
        NotificationDeliveryProviderCheck check =
                providerCheck(
                        NotificationDeliveryProviderCheckStatus.HEALTHY,
                        200
                );

        assertNull(check.getCreatedAt());
        assertNotNull(check.getCheckedAt());

        check.onCreate();

        assertNotNull(check.getCreatedAt());
        assertNotNull(check.getCheckedAt());
    }

    private NotificationDeliveryProviderCheck providerCheck(
            NotificationDeliveryProviderCheckStatus status,
            Integer httpStatusCode
    ) {
        return new NotificationDeliveryProviderCheck(
                NotificationDeliveryChannel.TELEGRAM,
                status,
                OffsetDateTime.now(),
                "Safe provider-check diagnostic.",
                httpStatusCode
        );
    }

    private static final class RecordingProviderCheckRepository {

        private NotificationDeliveryProviderCheck savedCheck;

        private int saveCallCount;

        private Optional<NotificationDeliveryProviderCheck> latestResult =
                Optional.empty();

        private List<NotificationDeliveryProviderCheck> recentResult =
                List.of();

        private NotificationDeliveryChannel latestRequestedChannel;

        private NotificationDeliveryChannel recentRequestedChannel;

        private int findLatestCallCount;

        private int findRecentCallCount;

        private NotificationDeliveryProviderCheckRepository asRepository() {
            return (NotificationDeliveryProviderCheckRepository)
                    Proxy.newProxyInstance(
                            NotificationDeliveryProviderCheckRepository.class
                                    .getClassLoader(),
                            new Class<?>[]{
                                    NotificationDeliveryProviderCheckRepository.class
                            },
                            (proxy, method, arguments) ->
                                    handleInvocation(
                                            proxy,
                                            method.getName(),
                                            arguments
                                    )
                    );
        }

        private Object handleInvocation(
                Object proxy,
                String methodName,
                Object[] arguments
        ) {
            return switch (methodName) {

                case "save" -> {
                    saveCallCount++;

                    savedCheck =
                            (NotificationDeliveryProviderCheck)
                                    arguments[0];

                    yield savedCheck;
                }

                case "findFirstByChannelOrderByCheckedAtDesc" -> {
                    findLatestCallCount++;

                    latestRequestedChannel =
                            (NotificationDeliveryChannel)
                                    arguments[0];

                    yield latestResult;
                }

                case "findTop10ByChannelOrderByCheckedAtDesc" -> {
                    findRecentCallCount++;

                    recentRequestedChannel =
                            (NotificationDeliveryChannel)
                                    arguments[0];

                    yield recentResult;
                }

                case "toString" ->
                        "RecordingProviderCheckRepository";

                case "hashCode" ->
                        System.identityHashCode(proxy);

                case "equals" ->
                        proxy == arguments[0];

                default ->
                        throw new AssertionError(
                                "Unexpected repository method call: "
                                        + methodName
                        );
            };
        }

        private NotificationDeliveryProviderCheck getSavedCheck() {
            return savedCheck;
        }

        private int getSaveCallCount() {
            return saveCallCount;
        }

        private void setLatestResult(
                Optional<NotificationDeliveryProviderCheck> latestResult
        ) {
            this.latestResult = latestResult;
        }

        private void setRecentResult(
                List<NotificationDeliveryProviderCheck> recentResult
        ) {
            this.recentResult = recentResult;
        }

        private NotificationDeliveryChannel getLatestRequestedChannel() {
            return latestRequestedChannel;
        }

        private NotificationDeliveryChannel getRecentRequestedChannel() {
            return recentRequestedChannel;
        }

        private int getFindLatestCallCount() {
            return findLatestCallCount;
        }

        private int getFindRecentCallCount() {
            return findRecentCallCount;
        }
    }
}