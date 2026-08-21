package com.cigabyte.sitesentinel.scanner;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "sitesentinel.scanner")
public class ScannerProperties {

    private static final String DEFAULT_USER_AGENT =
            "SiteSentinel/1.0 "
                    + "(+https://sitesentinel.local; "
                    + "website trust monitoring)";

    private static final long MINIMUM_IN_MEMORY_BODY_THRESHOLD_BYTES =
            64L * 1024L;

    private static final int MAXIMUM_CRAWL_PAGES =
            100;

    private static final int MAXIMUM_CRAWL_DEPTH =
            5;

    private static final int MAXIMUM_ASSET_CHECKS =
            500;

    private static final int MAXIMUM_HEALTH_SCAN_REQUESTS =
            1000;

    private static final int
            MAXIMUM_HEALTH_SCAN_DURATION_SECONDS =
            300;

    private static final int MAXIMUM_LINKS_PER_PAGE =
            1000;

    private static final int MAXIMUM_ASSETS_PER_PAGE =
            1000;

    private long connectTimeoutSeconds = 8;

    private long requestTimeoutSeconds = 15;

    private String userAgent = DEFAULT_USER_AGENT;

    private boolean scanRobotsTxt = true;

    private boolean scanSitemapXml = true;

    private int bodySnippetMaxLength = 1000;

    private boolean allowPrivateTargets = false;

    private int maxRedirects = 5;

    private long inMemoryBodyThresholdBytes =
            1024L * 1024L;

    private String temporaryDirectory = "";

    private boolean healthScanEnabled = true;

    private int maxCrawlPages = 25;

    private int maxCrawlDepth = 2;

    private int maxAssetChecks = 100;

    private int maxHealthScanRequests = 150;

    private int maxHealthScanDurationSeconds = 60;

    private int maxLinksPerPage = 200;

    private int maxAssetsPerPage = 200;

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(
            long connectTimeoutSeconds
    ) {
        this.connectTimeoutSeconds =
                Math.max(1, connectTimeoutSeconds);
    }

    public long getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(
            long requestTimeoutSeconds
    ) {
        this.requestTimeoutSeconds =
                Math.max(1, requestTimeoutSeconds);
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            this.userAgent = DEFAULT_USER_AGENT;
            return;
        }

        this.userAgent = userAgent.trim();
    }

    public boolean isScanRobotsTxt() {
        return scanRobotsTxt;
    }

    public void setScanRobotsTxt(
            boolean scanRobotsTxt
    ) {
        this.scanRobotsTxt = scanRobotsTxt;
    }

    public boolean isScanSitemapXml() {
        return scanSitemapXml;
    }

    public void setScanSitemapXml(
            boolean scanSitemapXml
    ) {
        this.scanSitemapXml = scanSitemapXml;
    }

    public int getBodySnippetMaxLength() {
        return bodySnippetMaxLength;
    }

    public void setBodySnippetMaxLength(
            int bodySnippetMaxLength
    ) {
        this.bodySnippetMaxLength =
                Math.max(100, bodySnippetMaxLength);
    }

    public boolean isAllowPrivateTargets() {
        return allowPrivateTargets;
    }

    public void setAllowPrivateTargets(
            boolean allowPrivateTargets
    ) {
        this.allowPrivateTargets =
                allowPrivateTargets;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(
            int maxRedirects
    ) {
        this.maxRedirects =
                Math.max(0, maxRedirects);
    }

    public long getInMemoryBodyThresholdBytes() {
        return inMemoryBodyThresholdBytes;
    }

    public void setInMemoryBodyThresholdBytes(
            long inMemoryBodyThresholdBytes
    ) {
        this.inMemoryBodyThresholdBytes =
                Math.max(
                        MINIMUM_IN_MEMORY_BODY_THRESHOLD_BYTES,
                        inMemoryBodyThresholdBytes
                );
    }

    public String getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public void setTemporaryDirectory(
            String temporaryDirectory
    ) {
        this.temporaryDirectory =
                temporaryDirectory == null
                        ? ""
                        : temporaryDirectory.trim();
    }

    public boolean hasConfiguredTemporaryDirectory() {
        return StringUtils.hasText(
                temporaryDirectory
        );
    }

    public boolean isHealthScanEnabled() {
        return healthScanEnabled;
    }

    public void setHealthScanEnabled(
            boolean healthScanEnabled
    ) {
        this.healthScanEnabled =
                healthScanEnabled;
    }

    public int getMaxCrawlPages() {
        return maxCrawlPages;
    }

    public void setMaxCrawlPages(
            int maxCrawlPages
    ) {
        this.maxCrawlPages =
                clamp(
                        maxCrawlPages,
                        1,
                        MAXIMUM_CRAWL_PAGES
                );
    }

    public int getMaxCrawlDepth() {
        return maxCrawlDepth;
    }

    public void setMaxCrawlDepth(
            int maxCrawlDepth
    ) {
        this.maxCrawlDepth =
                clamp(
                        maxCrawlDepth,
                        0,
                        MAXIMUM_CRAWL_DEPTH
                );
    }

    public int getMaxAssetChecks() {
        return maxAssetChecks;
    }

    public void setMaxAssetChecks(
            int maxAssetChecks
    ) {
        this.maxAssetChecks =
                clamp(
                        maxAssetChecks,
                        1,
                        MAXIMUM_ASSET_CHECKS
                );
    }

    public int getMaxHealthScanRequests() {
        return maxHealthScanRequests;
    }

    public void setMaxHealthScanRequests(
            int maxHealthScanRequests
    ) {
        this.maxHealthScanRequests =
                clamp(
                        maxHealthScanRequests,
                        1,
                        MAXIMUM_HEALTH_SCAN_REQUESTS
                );
    }

    public int getMaxHealthScanDurationSeconds() {
        return maxHealthScanDurationSeconds;
    }

    public void setMaxHealthScanDurationSeconds(
            int maxHealthScanDurationSeconds
    ) {
        this.maxHealthScanDurationSeconds =
                clamp(
                        maxHealthScanDurationSeconds,
                        1,
                        MAXIMUM_HEALTH_SCAN_DURATION_SECONDS
                );
    }

    public int getMaxLinksPerPage() {
        return maxLinksPerPage;
    }

    public void setMaxLinksPerPage(
            int maxLinksPerPage
    ) {
        this.maxLinksPerPage =
                clamp(
                        maxLinksPerPage,
                        1,
                        MAXIMUM_LINKS_PER_PAGE
                );
    }

    public int getMaxAssetsPerPage() {
        return maxAssetsPerPage;
    }

    public void setMaxAssetsPerPage(
            int maxAssetsPerPage
    ) {
        this.maxAssetsPerPage =
                clamp(
                        maxAssetsPerPage,
                        1,
                        MAXIMUM_ASSETS_PER_PAGE
                );
    }

    private int clamp(
            int value,
            int minimum,
            int maximum
    ) {
        return Math.max(
                minimum,
                Math.min(
                        value,
                        maximum
                )
        );
    }
}