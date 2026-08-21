package com.cigabyte.sitesentinel.engine.collection.health;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public record CrawlUrlNormalizationResult(
        CrawlUrlNormalizationStatus status,
        Optional<URI> normalizedUri
) {

    public CrawlUrlNormalizationResult {
        status =
                Objects.requireNonNull(
                        status,
                        "Normalization status is required."
                );

        normalizedUri =
                Objects.requireNonNull(
                        normalizedUri,
                        "Normalized URI optional "
                                + "value is required."
                );

        if (status
                == CrawlUrlNormalizationStatus.ACCEPTED
                && normalizedUri.isEmpty()) {

            throw new IllegalArgumentException(
                    "An accepted normalization result "
                            + "must contain a URI."
            );
        }
    }

    public static CrawlUrlNormalizationResult accepted(
            URI normalizedUri
    ) {
        return new CrawlUrlNormalizationResult(
                CrawlUrlNormalizationStatus.ACCEPTED,
                Optional.of(
                        Objects.requireNonNull(
                                normalizedUri,
                                "Accepted normalized URI "
                                        + "is required."
                        )
                )
        );
    }

    public static CrawlUrlNormalizationResult rejected(
            CrawlUrlNormalizationStatus status
    ) {
        return rejected(
                status,
                Optional.empty()
        );
    }

    public static CrawlUrlNormalizationResult rejected(
            CrawlUrlNormalizationStatus status,
            URI normalizedUri
    ) {
        return rejected(
                status,
                Optional.of(
                        Objects.requireNonNull(
                                normalizedUri,
                                "Rejected normalized URI "
                                        + "is required."
                        )
                )
        );
    }

    public boolean isAccepted() {
        return status
                == CrawlUrlNormalizationStatus.ACCEPTED;
    }

    private static CrawlUrlNormalizationResult rejected(
            CrawlUrlNormalizationStatus status,
            Optional<URI> normalizedUri
    ) {
        Objects.requireNonNull(
                status,
                "Rejected normalization status "
                        + "is required."
        );

        if (status
                == CrawlUrlNormalizationStatus.ACCEPTED) {

            throw new IllegalArgumentException(
                    "Accepted status cannot be used "
                            + "for a rejected result."
            );
        }

        return new CrawlUrlNormalizationResult(
                status,
                normalizedUri
        );
    }
}