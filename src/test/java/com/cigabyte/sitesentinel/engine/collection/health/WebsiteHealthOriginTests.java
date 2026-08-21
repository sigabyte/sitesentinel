package com.cigabyte.sitesentinel.engine.collection.health;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebsiteHealthOriginTests {

    @Test
    void httpsDefaultPortIsCanonicalized() {
        WebsiteHealthOrigin implicitPort =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://Example.COM/path"
                        )
                );

        WebsiteHealthOrigin explicitPort =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://example.com:443/other"
                        )
                );

        assertEquals(
                implicitPort,
                explicitPort
        );

        assertEquals(
                "https",
                implicitPort.scheme()
        );

        assertEquals(
                "example.com",
                implicitPort.host()
        );

        assertEquals(
                443,
                implicitPort.port()
        );
    }

    @Test
    void httpDefaultPortIsCanonicalized() {
        WebsiteHealthOrigin implicitPort =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "http://example.com"
                        )
                );

        WebsiteHealthOrigin explicitPort =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "http://example.com:80/"
                        )
                );

        assertEquals(
                implicitPort,
                explicitPort
        );

        assertEquals(
                80,
                implicitPort.port()
        );
    }

    @Test
    void schemeHostAndEffectivePortDefineOriginIdentity() {
        WebsiteHealthOrigin baseline =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://example.com/"
                        )
                );

        assertNotEquals(
                baseline,
                WebsiteHealthOrigin.from(
                        URI.create(
                                "http://example.com/"
                        )
                )
        );

        assertNotEquals(
                baseline,
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://www.example.com/"
                        )
                )
        );

        assertNotEquals(
                baseline,
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://example.com:8443/"
                        )
                )
        );
    }

    @Test
    void unsupportedSchemeIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> WebsiteHealthOrigin.from(
                        URI.create(
                                "ftp://example.com/file"
                        )
                )
        );
    }

    @Test
    void missingHostIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> WebsiteHealthOrigin.from(
                        URI.create(
                                "https:/relative-path"
                        )
                )
        );
    }

    @Test
    void embeddedCredentialsAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> WebsiteHealthOrigin.from(
                        URI.create(
                                "https://user:password@example.com/"
                        )
                )
        );
    }

    @Test
    void nullUriIsRejected() {
        assertThrows(
                NullPointerException.class,
                () -> WebsiteHealthOrigin.from(
                        null
                )
        );
    }

    @Test
    void trailingDnsDotDoesNotCreateDifferentOrigin() {
        WebsiteHealthOrigin regularOrigin =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://example.com/"
                        )
                );

        WebsiteHealthOrigin trailingDotOrigin =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://example.com./page"
                        )
                );

        assertEquals(
                regularOrigin,
                trailingDotOrigin
        );
    }

    @Test
    void ipv6BracketsAreNotPartOfOriginHostIdentity() {
        WebsiteHealthOrigin origin =
                WebsiteHealthOrigin.from(
                        URI.create(
                                "https://[2001:db8::1]/"
                        )
                );

        assertEquals(
                "2001:db8::1",
                origin.host()
        );

        assertEquals(
                443,
                origin.port()
        );
    }

    @Test
    void invalidExplicitPortIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> WebsiteHealthOrigin.from(
                        URI.create(
                                "https://example.com:0/"
                        )
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WebsiteHealthOrigin(
                        "https",
                        "example.com",
                        65536
                )
        );
    }
}