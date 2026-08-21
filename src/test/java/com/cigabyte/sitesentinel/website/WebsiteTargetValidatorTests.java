package com.cigabyte.sitesentinel.website;

import com.cigabyte.sitesentinel.scanner.ScannerProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebsiteTargetValidatorTests {

    private final WebsiteTargetValidator validator =
            new WebsiteTargetValidator(
                    new ScannerProperties()
            );

    @Test
    void publicAddressInside192DotZeroSixteenIsAllowed() {
        assertDoesNotThrow(
                () -> validator.validateConfiguredHost(
                        "192.0.66.175"
                )
        );
    }

    @Test
    void specialUse192DotZeroDotZeroRangeIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateConfiguredHost(
                        "192.0.0.8"
                )
        );
    }

    @Test
    void private192Dot168RangeRemainsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateConfiguredHost(
                        "192.168.1.10"
                )
        );
    }
}
