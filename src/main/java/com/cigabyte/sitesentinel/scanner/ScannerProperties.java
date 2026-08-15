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
}