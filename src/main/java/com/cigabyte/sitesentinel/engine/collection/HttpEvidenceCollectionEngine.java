package com.cigabyte.sitesentinel.engine.collection;

import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.scanner.ScannerProperties;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import com.cigabyte.sitesentinel.website.WebsiteTargetValidator;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


@Service
public class HttpEvidenceCollectionEngine implements EvidenceCollectionEngine {

    private final MonitoringRunRepository monitoringRunRepository;
    private final WebsiteRepository websiteRepository;
    private final WebsiteTargetValidator websiteTargetValidator;
    private final EvidenceService evidenceService;
    private final HttpClient httpClient;
    private final Duration requestTimeout;
    private final String userAgent;
    private final boolean scanRobotsTxt;
    private final boolean scanSitemapXml;
    private final int bodySnippetMaxLength;
    private final int maxRedirects;
    private final AdaptiveResponseBodyHandler responseBodyHandler;
    private final StreamingResponseBodyAnalyzer responseBodyAnalyzer;


    public HttpEvidenceCollectionEngine(
            MonitoringRunRepository monitoringRunRepository,
            WebsiteRepository websiteRepository,
            WebsiteTargetValidator websiteTargetValidator,
            EvidenceService evidenceService,
            ScannerProperties scannerProperties,
            AdaptiveResponseBodyHandler responseBodyHandler,
            StreamingResponseBodyAnalyzer responseBodyAnalyzer
    ) {
        this.monitoringRunRepository = monitoringRunRepository;
        this.websiteRepository = websiteRepository;
        this.websiteTargetValidator = websiteTargetValidator;
        this.evidenceService = evidenceService;
        this.responseBodyHandler = responseBodyHandler;
        this.responseBodyAnalyzer = responseBodyAnalyzer;

        Duration connectTimeout = Duration.ofSeconds(
                scannerProperties.getConnectTimeoutSeconds()
        );

        this.requestTimeout = Duration.ofSeconds(
                scannerProperties.getRequestTimeoutSeconds()
        );

        this.userAgent = scannerProperties.getUserAgent();
        this.scanRobotsTxt =
                scannerProperties.isScanRobotsTxt();
        this.scanSitemapXml =
                scannerProperties.isScanSitemapXml();
        this.bodySnippetMaxLength =
                scannerProperties.getBodySnippetMaxLength();
        this.maxRedirects =
                scannerProperties.getMaxRedirects();

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @Override
    public void collect(
            UUID monitoringRunId
    ) {
        MonitoringRun monitoringRun =
                monitoringRunRepository
                        .findById(monitoringRunId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Monitoring run not found: "
                                                + monitoringRunId
                                )
                        );

        Website website =
                websiteRepository
                        .findById(
                                monitoringRun.getWebsiteId()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Website not found: "
                                                + monitoringRun
                                                .getWebsiteId()
                                )
                        );

        validateScannerTarget(
                website,
                monitoringRun
        );

        String origin;

        try (CollectedHttpResponse homepageResponse =
                     scanHomepage(
                             website,
                             monitoringRun
                     )) {

            origin = resolveOrigin(
                    homepageResponse.getUri(),
                    website
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to release homepage response body "
                            + "for domain: "
                            + website.getDomain(),
                    exception
            );
        }

        String robotsTxtUrl =
                origin + "/robots.txt";

        String sitemapXmlUrl =
                origin + "/sitemap.xml";

        if (scanRobotsTxt) {
            scanOptionalResource(
                    website,
                    monitoringRun,
                    robotsTxtUrl,
                    "ROBOTS_TXT"
            );
        } else {
            recordOptionalResourceSkipped(
                    website,
                    monitoringRun,
                    robotsTxtUrl,
                    "ROBOTS_TXT"
            );
        }

        if (scanSitemapXml) {
            scanOptionalResource(
                    website,
                    monitoringRun,
                    sitemapXmlUrl,
                    "SITEMAP_XML"
            );
        } else {
            recordOptionalResourceSkipped(
                    website,
                    monitoringRun,
                    sitemapXmlUrl,
                    "SITEMAP_XML"
            );
        }
    }

    private CollectedHttpResponse scanHomepage(
            Website website,
            MonitoringRun monitoringRun
    ) {
        String httpsUrl =
                "https://"
                        + website.getDomain()
                        + "/";

        try {
            return fetchAndRecordHomepage(
                    website,
                    monitoringRun,
                    httpsUrl
            );
        } catch (RuntimeException exception) {
            record(
                    website,
                    monitoringRun,
                    "HOMEPAGE",
                    "FETCH_OUTCOME",
                    httpsUrl,
                    "FAILED"
            );

            record(
                    website,
                    monitoringRun,
                    "HOMEPAGE",
                    "FETCH_ERROR",
                    httpsUrl,
                    safeMessage(exception)
            );

            String httpUrl =
                    "http://"
                            + website.getDomain()
                            + "/";

            try {
                return fetchAndRecordHomepage(
                        website,
                        monitoringRun,
                        httpUrl
                );
            } catch (RuntimeException fallbackException) {
                record(
                        website,
                        monitoringRun,
                        "HOMEPAGE",
                        "FETCH_OUTCOME",
                        httpUrl,
                        "FAILED"
                );

                record(
                        website,
                        monitoringRun,
                        "HOMEPAGE",
                        "FETCH_ERROR",
                        httpUrl,
                        safeMessage(
                                fallbackException
                        )
                );

                throw new IllegalStateException(
                        "Homepage fetch failed for both "
                                + "HTTPS and HTTP for domain: "
                                + website.getDomain(),
                        fallbackException
                );
            }
        }
    }

    private CollectedHttpResponse
    fetchAndRecordHomepage(
            Website website,
            MonitoringRun monitoringRun,
            String url
    ) {
        CollectedHttpResponse response =
                fetch(url);

        try {
            record(
                    website,
                    monitoringRun,
                    "HOMEPAGE",
                    "FETCH_OUTCOME",
                    url,
                    "SUCCESS"
            );

            recordResponseEvidence(
                    website,
                    monitoringRun,
                    "HOMEPAGE",
                    url,
                    response
            );

            recordHomepageHtmlEvidence(
                    website,
                    monitoringRun,
                    response
            );

            return response;
        } catch (RuntimeException exception) {
            closeResponseAfterFailure(
                    response,
                    exception
            );

            throw exception;
        }
    }

    private void scanOptionalResource(
            Website website,
            MonitoringRun monitoringRun,
            String url,
            String sourceType
    ) {
        CollectedHttpResponse response = null;

        try {
            response = fetch(url);

            record(
                    website,
                    monitoringRun,
                    sourceType,
                    "FETCH_OUTCOME",
                    url,
                    "SUCCESS"
            );

            recordResponseEvidence(
                    website,
                    monitoringRun,
                    sourceType,
                    url,
                    response
            );

            String contentType =
                    firstHeader(
                            response.getHeaders(),
                            "content-type"
                    )
                            .orElse("");

            ResponseBodyAnalysisResult analysisResult =
                    analyzeResponseBody(
                            response,
                            contentType
                    );

            recordBodyAnalysisEvidence(
                    website,
                    monitoringRun,
                    sourceType,
                    url,
                    analysisResult
            );
        } catch (RuntimeException exception) {
            record(
                    website,
                    monitoringRun,
                    sourceType,
                    "FETCH_OUTCOME",
                    url,
                    "FAILED"
            );

            record(
                    website,
                    monitoringRun,
                    sourceType,
                    "FETCH_ERROR",
                    url,
                    safeMessage(exception)
            );
        } finally {
            closeOptionalResponse(
                    response,
                    website,
                    monitoringRun,
                    sourceType,
                    url
            );
        }
    }

    private void recordOptionalResourceSkipped(
            Website website,
            MonitoringRun monitoringRun,
            String url,
            String sourceType
    ) {
        record(
                website,
                monitoringRun,
                sourceType,
                "FETCH_OUTCOME",
                url,
                "SKIPPED"
        );

        record(
                website,
                monitoringRun,
                sourceType,
                "SCAN_SKIPPED_REASON",
                url,
                "Disabled by scanner configuration."
        );
    }

    private CollectedHttpResponse fetch(
            String url
    ) {
        URI currentUri =
                URI.create(url);

        for (int redirectCount = 0;
             redirectCount <= maxRedirects;
             redirectCount++) {

            validateUriTarget(
                    currentUri
            );

            try {
                HttpRequest request =
                        HttpRequest
                                .newBuilder(currentUri)
                                .timeout(
                                        requestTimeout
                                )
                                .header(
                                        "User-Agent",
                                        userAgent
                                )
                                .header(
                                        "Accept",
                                        "text/html,"
                                                + "application/xhtml+xml,"
                                                + "application/xml;q=0.9,"
                                                + "*/*;q=0.8"
                                )
                                .GET()
                                .build();

                HttpResponse<StoredResponseBody>
                        httpResponse =
                        httpClient.send(
                                request,
                                responseBodyHandler
                        );

                CollectedHttpResponse response =
                        new CollectedHttpResponse(
                                httpResponse.statusCode(),
                                httpResponse.uri(),
                                httpResponse.headers(),
                                httpResponse.body()
                        );

                if (!isRedirectStatus(
                        response.getStatusCode()
                )) {
                    return response;
                }

                Optional<String> location =
                        response.getHeaders()
                                .firstValue(
                                        "location"
                                );

                if (location.isEmpty()
                        || location.get().isBlank()) {

                    return response;
                }

                URI nextUri;

                try {
                    nextUri =
                            resolveRedirectUri(
                                    currentUri,
                                    location.get()
                            );
                } catch (RuntimeException exception) {
                    closeResponseAfterFailure(
                            response,
                            exception
                    );

                    throw exception;
                }

                closeRedirectResponse(
                        response
                );

                currentUri =
                        nextUri;
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "HTTP request failed: "
                                + currentUri,
                        exception
                );
            } catch (InterruptedException exception) {
                Thread.currentThread()
                        .interrupt();

                throw new IllegalStateException(
                        "HTTP request interrupted: "
                                + currentUri,
                        exception
                );
            } catch (IllegalArgumentException exception) {
                throw new IllegalStateException(
                        "Invalid or unsafe URL: "
                                + currentUri,
                        exception
                );
            }
        }

        throw new IllegalStateException(
                "Maximum redirect limit exceeded: "
                        + url
        );
    }

    private void recordResponseEvidence(
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String requestedUrl,
            CollectedHttpResponse response
    ) {
        record(
                website,
                monitoringRun,
                sourceType,
                "REQUESTED_URL",
                requestedUrl,
                requestedUrl
        );

        record(
                website,
                monitoringRun,
                sourceType,
                "HTTP_STATUS",
                requestedUrl,
                String.valueOf(
                        response.getStatusCode()
                )
        );

        record(
                website,
                monitoringRun,
                sourceType,
                "FINAL_URL",
                requestedUrl,
                response.getUri()
                        .toString()
        );

        String contentType =
                firstHeader(
                        response.getHeaders(),
                        "content-type"
                )
                        .orElse("MISSING");

        record(
                website,
                monitoringRun,
                sourceType,
                "CONTENT_TYPE",
                requestedUrl,
                contentType
        );

        recordSecurityHeader(
                website,
                monitoringRun,
                sourceType,
                requestedUrl,
                response.getHeaders(),
                "strict-transport-security",
                "HEADER_STRICT_TRANSPORT_SECURITY"
        );

        recordSecurityHeader(
                website,
                monitoringRun,
                sourceType,
                requestedUrl,
                response.getHeaders(),
                "content-security-policy",
                "HEADER_CONTENT_SECURITY_POLICY"
        );

        recordSecurityHeader(
                website,
                monitoringRun,
                sourceType,
                requestedUrl,
                response.getHeaders(),
                "x-frame-options",
                "HEADER_X_FRAME_OPTIONS"
        );

        recordSecurityHeader(
                website,
                monitoringRun,
                sourceType,
                requestedUrl,
                response.getHeaders(),
                "x-content-type-options",
                "HEADER_X_CONTENT_TYPE_OPTIONS"
        );

        recordSecurityHeader(
                website,
                monitoringRun,
                sourceType,
                requestedUrl,
                response.getHeaders(),
                "referrer-policy",
                "HEADER_REFERRER_POLICY"
        );
    }

    private void recordHomepageHtmlEvidence(
            Website website,
            MonitoringRun monitoringRun,
            CollectedHttpResponse response
    ) {
        String sourceUrl =
                response.getUri()
                        .toString();

        String contentType =
                firstHeader(
                        response.getHeaders(),
                        "content-type"
                )
                        .orElse("");

        ResponseBodyAnalysisResult analysisResult =
                analyzeResponseBody(
                        response,
                        contentType
                );

        recordBodyAnalysisEvidence(
                website,
                monitoringRun,
                "HOMEPAGE",
                sourceUrl,
                analysisResult
        );

        if (!contentType
                .toLowerCase(Locale.ROOT)
                .contains("text/html")) {

            record(
                    website,
                    monitoringRun,
                    "HOMEPAGE",
                    "HTML_PARSE_SKIPPED",
                    sourceUrl,
                    "CONTENT_TYPE_NOT_HTML"
            );

            return;
        }

        record(
                website,
                monitoringRun,
                "HOMEPAGE",
                "PAGE_TITLE",
                sourceUrl,
                analysisResult
                        .pageTitle()
                        .orElse("MISSING")
        );

        record(
                website,
                monitoringRun,
                "HOMEPAGE",
                "META_DESCRIPTION",
                sourceUrl,
                analysisResult
                        .metaDescription()
                        .orElse("MISSING")
        );

        record(
                website,
                monitoringRun,
                "HOMEPAGE",
                "CANONICAL_URL",
                sourceUrl,
                analysisResult
                        .canonicalUrl()
                        .orElse("MISSING")
        );
    }

    private void recordBodyAnalysisEvidence(
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String sourceUrl,
            ResponseBodyAnalysisResult analysisResult
    ) {
        record(
                website,
                monitoringRun,
                sourceType,
                "BODY_LENGTH",
                sourceUrl,
                String.valueOf(
                        analysisResult
                                .characterLength()
                )
        );

        record(
                website,
                monitoringRun,
                sourceType,
                "BODY_SHA256",
                sourceUrl,
                analysisResult.sha256()
        );

        analysisResult
                .bodySnippet()
                .ifPresent(
                        snippet -> record(
                                website,
                                monitoringRun,
                                sourceType,
                                "BODY_SNIPPET",
                                sourceUrl,
                                snippet
                        )
                );
    }

    private ResponseBodyAnalysisResult
    analyzeResponseBody(
            CollectedHttpResponse response,
            String contentType
    ) {
        try {
            return responseBodyAnalyzer.analyze(
                    response,
                    StandardCharsets.UTF_8,
                    contentType,
                    bodySnippetMaxLength
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to analyze HTTP response body: "
                            + response.getUri(),
                    exception
            );
        }
    }

    private void closeRedirectResponse(
            CollectedHttpResponse response
    ) {
        try {
            response.close();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to release redirected "
                            + "HTTP response body: "
                            + response.getUri(),
                    exception
            );
        }
    }

    private void closeResponseAfterFailure(
            CollectedHttpResponse response,
            RuntimeException originalException
    ) {
        try {
            response.close();
        } catch (IOException closeException) {
            originalException.addSuppressed(
                    closeException
            );
        }
    }

    private void closeOptionalResponse(
            CollectedHttpResponse response,
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String sourceUrl
    ) {
        if (response == null) {
            return;
        }

        try {
            response.close();
        } catch (IOException exception) {
            record(
                    website,
                    monitoringRun,
                    sourceType,
                    "FETCH_ERROR",
                    sourceUrl,
                    "Failed to release collected "
                            + "HTTP response body."
            );
        }
    }

    private void recordSecurityHeader(
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String sourceUrl,
            HttpHeaders headers,
            String headerName,
            String evidenceType
    ) {
        String value = firstHeader(headers, headerName).orElse("MISSING");
        record(website, monitoringRun, sourceType, evidenceType, sourceUrl, value);
    }

    private Optional<String> firstHeader(HttpHeaders headers, String headerName) {
        return headers.firstValue(headerName)
                .map(String::trim)
                .filter(value -> !value.isBlank());
    }

    private String resolveOrigin(URI finalUri, Website website) {
        String scheme = finalUri.getScheme();

        if (scheme == null || scheme.isBlank()) {
            scheme = "https";
        }

        String host = finalUri.getHost();

        if (host == null || host.isBlank()) {
            host = website.getDomain();
        }

        int port = finalUri.getPort();

        if (port > 0) {
            return scheme + "://" + host + ":" + port;
        }

        return scheme + "://" + host;
    }

    private void record(
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String evidenceType,
            String sourceUrl,
            String rawValue
    ) {
        evidenceService.recordCollectedEvidence(
                website.getId(),
                monitoringRun.getId(),
                sourceType,
                evidenceType,
                truncate(sourceUrl, 500),
                rawValue == null ? "MISSING" : rawValue
        );
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }

        return truncate(message, 1000);
    }

    private void validateScannerTarget(Website website, MonitoringRun monitoringRun) {
        try {
            websiteTargetValidator.validateScanTarget(website.getDomain());

            record(
                    website,
                    monitoringRun,
                    "SCAN_TARGET",
                    "TARGET_ACCEPTED",
                    "https://" + website.getDomain() + "/",
                    website.getDomain()
            );
        } catch (IllegalArgumentException exception) {
            record(
                    website,
                    monitoringRun,
                    "SCAN_TARGET",
                    "TARGET_REJECTED",
                    "https://" + website.getDomain() + "/",
                    safeMessage(exception)
            );

            throw exception;
        }
    }

    private void validateUriTarget(URI uri) {
        String scheme = uri.getScheme();

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Only HTTP and HTTPS scanner targets are allowed.");
        }

        String host = uri.getHost();

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Scanner target host is required.");
        }

        websiteTargetValidator.validateScanTarget(host);
    }

    private URI resolveRedirectUri(URI currentUri, String locationHeader) {
        URI nextUri = currentUri.resolve(locationHeader.trim());

        String scheme = nextUri.getScheme();

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Redirect target must use HTTP or HTTPS.");
        }

        return nextUri;
    }

    private boolean isRedirectStatus(int statusCode) {
        return statusCode == 301
                || statusCode == 302
                || statusCode == 303
                || statusCode == 307
                || statusCode == 308;
    }
}