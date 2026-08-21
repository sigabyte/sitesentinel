package com.cigabyte.sitesentinel.engine.collection.health;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WebsiteHealthScanBudgetTests {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(
                    Instant.parse(
                            "2026-08-21T10:00:00Z"
                    ),
                    ZoneOffset.UTC
            );

    @Test
    void pageAcquisitionWithinDepthAndCapacitySucceeds() {
        WebsiteHealthScanBudget budget =
                createBudget(
                        2,
                        2
                );

        assertTrue(
                budget.tryAcquirePage(0)
        );

        assertTrue(
                budget.tryAcquirePage(2)
        );

        assertEquals(
                2,
                budget.getAcquiredPageCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .isEmpty()
        );
    }

    @Test
    void pageAcquisitionStopsAtConfiguredPageLimit() {
        WebsiteHealthScanBudget budget =
                createBudget(
                        1,
                        2
                );

        assertTrue(
                budget.tryAcquirePage(0)
        );

        assertFalse(
                budget.tryAcquirePage(1)
        );

        assertEquals(
                1,
                budget.getAcquiredPageCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .contains(
                                WebsiteHealthScanLimitReason
                                        .PAGE_LIMIT_REACHED
                        )
        );
    }

    @Test
    void pageAboveDepthLimitIsRejectedWithoutConsumingCapacity() {
        WebsiteHealthScanBudget budget =
                createBudget(
                        2,
                        1
                );

        assertFalse(
                budget.tryAcquirePage(2)
        );

        assertEquals(
                0,
                budget.getAcquiredPageCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .contains(
                                WebsiteHealthScanLimitReason
                                        .DEPTH_LIMIT_REACHED
                        )
        );

        assertTrue(
                budget.tryAcquirePage(1)
        );

        assertEquals(
                1,
                budget.getAcquiredPageCount()
        );
    }

    @Test
    void separateBudgetsDoNotSharePageCounters() {
        WebsiteHealthScanBudget firstBudget =
                createBudget(
                        1,
                        1
                );

        WebsiteHealthScanBudget secondBudget =
                createBudget(
                        1,
                        1
                );

        assertTrue(
                firstBudget.tryAcquirePage(0)
        );

        assertEquals(
                1,
                firstBudget.getAcquiredPageCount()
        );

        assertEquals(
                0,
                secondBudget.getAcquiredPageCount()
        );

        assertTrue(
                secondBudget.tryAcquirePage(0)
        );
    }

    @Test
    void assetAcquisitionStopsAtConfiguredLimit() {
        WebsiteHealthScanBudget budget =
                new WebsiteHealthScanBudget(
                        2,
                        2,
                        1,
                        3,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                );

        assertTrue(
                budget.tryAcquireAsset()
        );

        assertFalse(
                budget.tryAcquireAsset()
        );

        assertEquals(
                1,
                budget.getAcquiredAssetCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .contains(
                                WebsiteHealthScanLimitReason
                                        .ASSET_LIMIT_REACHED
                        )
        );
    }

    @Test
    void rejectedAssetAcquisitionDoesNotExceedCounter() {
        WebsiteHealthScanBudget budget =
                new WebsiteHealthScanBudget(
                        2,
                        2,
                        1,
                        3,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                );

        assertTrue(
                budget.tryAcquireAsset()
        );

        assertFalse(
                budget.tryAcquireAsset()
        );

        assertFalse(
                budget.tryAcquireAsset()
        );

        assertEquals(
                1,
                budget.getAcquiredAssetCount()
        );
    }

    @Test
    void requestAcquisitionStopsAtConfiguredLimit() {
        WebsiteHealthScanBudget budget =
                new WebsiteHealthScanBudget(
                        2,
                        2,
                        3,
                        1,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                );

        assertTrue(
                budget.tryAcquireRequest()
        );

        assertFalse(
                budget.tryAcquireRequest()
        );

        assertEquals(
                1,
                budget.getAcquiredRequestCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .contains(
                                WebsiteHealthScanLimitReason
                                        .REQUEST_LIMIT_REACHED
                        )
        );
    }

    @Test
    void rejectedRequestAcquisitionDoesNotExceedCounter() {
        WebsiteHealthScanBudget budget =
                new WebsiteHealthScanBudget(
                        2,
                        2,
                        3,
                        1,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                );

        assertTrue(
                budget.tryAcquireRequest()
        );

        assertFalse(
                budget.tryAcquireRequest()
        );

        assertFalse(
                budget.tryAcquireRequest()
        );

        assertEquals(
                1,
                budget.getAcquiredRequestCount()
        );
    }

    @Test
    void acquisitionsWithinDurationLimitRemainAvailable() {
        MutableClock clock =
                new MutableClock(
                        Instant.parse(
                                "2026-08-21T10:00:00Z"
                        ),
                        ZoneOffset.UTC
                );

        WebsiteHealthScanBudget budget =
                new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        2,
                        Duration.ofSeconds(60),
                        clock
                );

        clock.advance(
                Duration.ofSeconds(59)
        );

        assertTrue(
                budget.tryAcquirePage(0)
        );

        assertTrue(
                budget.tryAcquireAsset()
        );

        assertTrue(
                budget.tryAcquireRequest()
        );

        assertFalse(
                budget.getLimitReasons()
                        .contains(
                                WebsiteHealthScanLimitReason
                                        .DURATION_LIMIT_REACHED
                        )
        );
    }

    @Test
    void durationExhaustionPreventsAllLaterAcquisitions() {
        MutableClock clock =
                new MutableClock(
                        Instant.parse(
                                "2026-08-21T10:00:00Z"
                        ),
                        ZoneOffset.UTC
                );

        WebsiteHealthScanBudget budget =
                new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        2,
                        Duration.ofSeconds(60),
                        clock
                );

        clock.advance(
                Duration.ofSeconds(60)
        );

        assertFalse(
                budget.tryAcquirePage(0)
        );

        assertFalse(
                budget.tryAcquireAsset()
        );

        assertFalse(
                budget.tryAcquireRequest()
        );

        assertEquals(
                0,
                budget.getAcquiredPageCount()
        );

        assertEquals(
                0,
                budget.getAcquiredAssetCount()
        );

        assertEquals(
                0,
                budget.getAcquiredRequestCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .contains(
                                WebsiteHealthScanLimitReason
                                        .DURATION_LIMIT_REACHED
                        )
        );
    }

    @Test
    void constructorRejectsInvalidLimitsAndDependencies() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthScanBudget(
                        0,
                        2,
                        2,
                        2,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        -1,
                        2,
                        2,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        2,
                        0,
                        2,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        0,
                        Duration.ofSeconds(60),
                        FIXED_CLOCK
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        2,
                        Duration.ZERO,
                        FIXED_CLOCK
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        2,
                        Duration.ofSeconds(-1),
                        FIXED_CLOCK
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        2,
                        null,
                        FIXED_CLOCK
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new WebsiteHealthScanBudget(
                        2,
                        2,
                        2,
                        2,
                        Duration.ofSeconds(60),
                        null
                )
        );
    }

    @Test
    void negativePageDepthIsRejectedAsProgrammerError() {
        WebsiteHealthScanBudget budget =
                createBudget(
                        2,
                        2
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> budget.tryAcquirePage(-1)
        );

        assertEquals(
                0,
                budget.getAcquiredPageCount()
        );

        assertTrue(
                budget.getLimitReasons()
                        .isEmpty()
        );
    }

    private WebsiteHealthScanBudget createBudget(
            int maxPages,
            int maxDepth
    ) {
        return new WebsiteHealthScanBudget(
                maxPages,
                maxDepth,
                10,
                20,
                Duration.ofSeconds(60),
                FIXED_CLOCK
        );
    }

    private static final class MutableClock
            extends Clock {

        private Instant currentInstant;
        private final ZoneId zone;

        private MutableClock(
                Instant currentInstant,
                ZoneId zone
        ) {
            this.currentInstant =
                    currentInstant;

            this.zone =
                    zone;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(
                ZoneId requestedZone
        ) {
            return new MutableClock(
                    currentInstant,
                    requestedZone
            );
        }

        @Override
        public Instant instant() {
            return currentInstant;
        }

        private void advance(
                Duration duration
        ) {
            currentInstant =
                    currentInstant.plus(
                            duration
                    );
        }
    }
}