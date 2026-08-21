package com.cigabyte.sitesentinel.engine.collection.health;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class WebsiteHealthScanBudget {

    private final int maxPages;
    private final int maxDepth;
    private final int maxAssets;
    private final int maxRequests;
    private final Duration maxDuration;
    private final Clock clock;
    private final Instant startedAt;

    private final Set<WebsiteHealthScanLimitReason>
            limitReasons =
            EnumSet.noneOf(
                    WebsiteHealthScanLimitReason.class
            );

    private int acquiredPageCount;

    private int acquiredAssetCount;

    private int acquiredRequestCount;

    public WebsiteHealthScanBudget(
            int maxPages,
            int maxDepth,
            int maxAssets,
            int maxRequests,
            Duration maxDuration,
            Clock clock
    ) {
        this.maxPages =
                requirePositive(
                        maxPages,
                        "Maximum page count"
                );

        this.maxDepth =
                requireNonNegative(
                        maxDepth,
                        "Maximum crawl depth"
                );

        this.maxAssets =
                requirePositive(
                        maxAssets,
                        "Maximum asset count"
                );

        this.maxRequests =
                requirePositive(
                        maxRequests,
                        "Maximum request count"
                );

        this.maxDuration =
                requirePositiveDuration(
                        maxDuration
                );

        this.clock =
                Objects.requireNonNull(
                        clock,
                        "Clock is required."
                );

        this.startedAt =
                clock.instant();
    }

    public boolean tryAcquirePage(
            int depth
    ) {
        if (depth < 0) {
            throw new IllegalArgumentException(
                    "Page depth cannot be negative."
            );
        }

        if (isDurationLimitReached()) {
            return false;
        }

        if (depth > maxDepth) {
            limitReasons.add(
                    WebsiteHealthScanLimitReason
                            .DEPTH_LIMIT_REACHED
            );

            return false;
        }

        if (acquiredPageCount >= maxPages) {
            limitReasons.add(
                    WebsiteHealthScanLimitReason
                            .PAGE_LIMIT_REACHED
            );

            return false;
        }

        acquiredPageCount++;

        return true;
    }

    public int getAcquiredPageCount() {
        return acquiredPageCount;
    }

    public boolean tryAcquireAsset() {
        if (isDurationLimitReached()) {
            return false;
        }

        if (acquiredAssetCount >= maxAssets) {
            limitReasons.add(
                    WebsiteHealthScanLimitReason
                            .ASSET_LIMIT_REACHED
            );

            return false;
        }

        acquiredAssetCount++;

        return true;
    }

    public int getAcquiredAssetCount() {
        return acquiredAssetCount;
    }

    public boolean tryAcquireRequest() {
        if (isDurationLimitReached()) {
            return false;
        }

        if (acquiredRequestCount >= maxRequests) {
            limitReasons.add(
                    WebsiteHealthScanLimitReason
                            .REQUEST_LIMIT_REACHED
            );

            return false;
        }

        acquiredRequestCount++;

        return true;
    }

    public int getAcquiredRequestCount() {
        return acquiredRequestCount;
    }

    public Set<WebsiteHealthScanLimitReason>
    getLimitReasons() {
        return Collections.unmodifiableSet(
                EnumSet.copyOf(
                        limitReasons
                )
        );
    }

    private boolean isDurationLimitReached() {
        Duration elapsed =
                Duration.between(
                        startedAt,
                        clock.instant()
                );

        if (elapsed.compareTo(
                maxDuration
        ) < 0) {
            return false;
        }

        limitReasons.add(
                WebsiteHealthScanLimitReason
                        .DURATION_LIMIT_REACHED
        );

        return true;
    }

    private int requirePositive(
            int value,
            String fieldName
    ) {
        if (value < 1) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be positive."
            );
        }

        return value;
    }

    private int requireNonNegative(
            int value,
            String fieldName
    ) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " cannot be negative."
            );
        }

        return value;
    }

    private Duration requirePositiveDuration(
            Duration duration
    ) {
        Objects.requireNonNull(
                duration,
                "Maximum scan duration is required."
        );

        if (duration.isZero()
                || duration.isNegative()) {

            throw new IllegalArgumentException(
                    "Maximum scan duration "
                            + "must be positive."
            );
        }

        return duration;
    }
}