package com.cigabyte.sitesentinel.scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class ScannerPropertiesTests {

    @Test
    void defaultsProvideControlledWebsiteHealthScanLimits() {
        ScannerProperties properties =
                new ScannerProperties();

        assertTrue(
                properties.isHealthScanEnabled()
        );

        assertEquals(
                25,
                properties.getMaxCrawlPages()
        );

        assertEquals(
                2,
                properties.getMaxCrawlDepth()
        );

        assertEquals(
                100,
                properties.getMaxAssetChecks()
        );

        assertEquals(
                150,
                properties.getMaxHealthScanRequests()
        );

        assertEquals(
                60,
                properties.getMaxHealthScanDurationSeconds()
        );

        assertEquals(
                200,
                properties.getMaxLinksPerPage()
        );

        assertEquals(
                200,
                properties.getMaxAssetsPerPage()
        );
    }

    @Test
    void healthScanEnablementCanBeConfigured() {
        ScannerProperties properties =
                new ScannerProperties();

        properties.setHealthScanEnabled(
                false
        );

        assertFalse(
                properties.isHealthScanEnabled()
        );

        properties.setHealthScanEnabled(
                true
        );

        assertTrue(
                properties.isHealthScanEnabled()
        );
    }

    @Test
    void invalidHealthScanLimitsAreClampedToSafeMinimums() {
        ScannerProperties properties =
                new ScannerProperties();

        properties.setMaxCrawlPages(0);
        properties.setMaxCrawlDepth(-1);
        properties.setMaxAssetChecks(0);
        properties.setMaxHealthScanRequests(0);
        properties.setMaxHealthScanDurationSeconds(0);
        properties.setMaxLinksPerPage(0);
        properties.setMaxAssetsPerPage(0);

        assertEquals(
                1,
                properties.getMaxCrawlPages()
        );

        assertEquals(
                0,
                properties.getMaxCrawlDepth()
        );

        assertEquals(
                1,
                properties.getMaxAssetChecks()
        );

        assertEquals(
                1,
                properties.getMaxHealthScanRequests()
        );

        assertEquals(
                1,
                properties.getMaxHealthScanDurationSeconds()
        );

        assertEquals(
                1,
                properties.getMaxLinksPerPage()
        );

        assertEquals(
                1,
                properties.getMaxAssetsPerPage()
        );
    }

    @Test
    void excessiveHealthScanLimitsAreClampedToSafetyCeilings() {
        ScannerProperties properties =
                new ScannerProperties();

        properties.setMaxCrawlPages(101);
        properties.setMaxCrawlDepth(6);
        properties.setMaxAssetChecks(501);
        properties.setMaxHealthScanRequests(1001);
        properties.setMaxHealthScanDurationSeconds(301);
        properties.setMaxLinksPerPage(1001);
        properties.setMaxAssetsPerPage(1001);

        assertEquals(
                100,
                properties.getMaxCrawlPages()
        );

        assertEquals(
                5,
                properties.getMaxCrawlDepth()
        );

        assertEquals(
                500,
                properties.getMaxAssetChecks()
        );

        assertEquals(
                1000,
                properties.getMaxHealthScanRequests()
        );

        assertEquals(
                300,
                properties.getMaxHealthScanDurationSeconds()
        );

        assertEquals(
                1000,
                properties.getMaxLinksPerPage()
        );

        assertEquals(
                1000,
                properties.getMaxAssetsPerPage()
        );
    }

    @Test
    void existingScannerDefaultsRemainPreserved() {
        ScannerProperties properties =
                new ScannerProperties();

        assertEquals(
                8,
                properties.getConnectTimeoutSeconds()
        );

        assertEquals(
                15,
                properties.getRequestTimeoutSeconds()
        );

        assertTrue(
                properties.isScanRobotsTxt()
        );

        assertTrue(
                properties.isScanSitemapXml()
        );

        assertEquals(
                1000,
                properties.getBodySnippetMaxLength()
        );

        assertFalse(
                properties.isAllowPrivateTargets()
        );

        assertEquals(
                5,
                properties.getMaxRedirects()
        );

        assertEquals(
                1024L * 1024L,
                properties.getInMemoryBodyThresholdBytes()
        );

        assertFalse(
                properties.hasConfiguredTemporaryDirectory()
        );
    }

    @Test
    void applicationConfigurationExposesControlledHealthScanDefaults()
            throws IOException {

        String configuration =
                loadApplicationConfiguration();

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "health-scan-enabled=true"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-crawl-pages=25"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-crawl-depth=2"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-asset-checks=100"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-health-scan-requests=150"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-health-scan-duration-seconds=60"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-links-per-page=200"
                )
        );

        assertTrue(
                configuration.contains(
                        "sitesentinel.scanner."
                                + "max-assets-per-page=200"
                )
        );
    }

    private String loadApplicationConfiguration()
            throws IOException {

        try (
                InputStream inputStream =
                        getClass().getResourceAsStream(
                                "/application.properties"
                        )
        ) {
            assertNotNull(
                    inputStream,
                    "Application configuration must exist "
                            + "on the classpath."
            );

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}