package com.cigabyte.sitesentinel.engine.collection.health;

public enum CrawlUrlNormalizationStatus {

    ACCEPTED,

    CROSS_ORIGIN,

    EMPTY_REFERENCE,

    MALFORMED_REFERENCE,

    UNSUPPORTED_SCHEME,

    EMBEDDED_CREDENTIALS,

    MISSING_HOST
}