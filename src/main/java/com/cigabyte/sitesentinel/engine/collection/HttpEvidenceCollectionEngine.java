package com.cigabyte.sitesentinel.engine.collection;

import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.monitoring.MonitoringRun;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunRepository;
import com.cigabyte.sitesentinel.website.Website;
import com.cigabyte.sitesentinel.website.WebsiteRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import com.cigabyte.sitesentinel.website.WebsiteTargetValidator;

@Service
public class HttpEvidenceCollectionEngine implements EvidenceCollectionEngine {

    private static final String DEFAULT_USER_AGENT =
            "SiteSentinel/1.0 (+https://sitesentinel.local; website trust monitoring)";

    private static final int DEFAULT_BODY_SNIPPET_MAX_LENGTH = 1000;

    private static final Pattern TITLE_PATTERN =
            Pattern.compile("(?is)<title[^>]*>(.*?)</title>");

    private static final Pattern META_DESCRIPTION_PATTERN_ONE =
            Pattern.compile("(?is)<meta[^>]+name=[\"']description[\"'][^>]+content=[\"']([^\"']*)[\"'][^>]*>");

    private static final Pattern META_DESCRIPTION_PATTERN_TWO =
            Pattern.compile("(?is)<meta[^>]+content=[\"']([^\"']*)[\"'][^>]+name=[\"']description[\"'][^>]*>");

    private static final Pattern CANONICAL_PATTERN_ONE =
            Pattern.compile("(?is)<link[^>]+rel=[\"'][^\"']*canonical[^\"']*[\"'][^>]+href=[\"']([^\"']*)[\"'][^>]*>");

    private static final Pattern CANONICAL_PATTERN_TWO =
            Pattern.compile("(?is)<link[^>]+href=[\"']([^\"']*)[\"'][^>]+rel=[\"'][^\"']*canonical[^\"']*[\"'][^>]*>");

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

    public HttpEvidenceCollectionEngine(
            MonitoringRunRepository monitoringRunRepository,
            WebsiteRepository websiteRepository,
            WebsiteTargetValidator websiteTargetValidator,
            EvidenceService evidenceService,
            @Value("${sitesentinel.scanner.connect-timeout-seconds:8}") long connectTimeoutSeconds,
            @Value("${sitesentinel.scanner.request-timeout-seconds:15}") long requestTimeoutSeconds,
            @Value("${sitesentinel.scanner.user-agent:}") String configuredUserAgent,
            @Value("${sitesentinel.scanner.scan-robots-txt:true}") boolean scanRobotsTxt,
            @Value("${sitesentinel.scanner.scan-sitemap-xml:true}") boolean scanSitemapXml,
            @Value("${sitesentinel.scanner.body-snippet-max-length:1000}") int configuredBodySnippetMaxLength,
            @Value("${sitesentinel.scanner.max-redirects:5}") int configuredMaxRedirects
    ) {
        this.monitoringRunRepository = monitoringRunRepository;
        this.websiteRepository = websiteRepository;
        this.websiteTargetValidator = websiteTargetValidator;
        this.evidenceService = evidenceService;

        Duration connectTimeout = Duration.ofSeconds(Math.max(1, connectTimeoutSeconds));
        this.requestTimeout = Duration.ofSeconds(Math.max(1, requestTimeoutSeconds));
        this.userAgent = cleanUserAgent(configuredUserAgent);
        this.scanRobotsTxt = scanRobotsTxt;
        this.scanSitemapXml = scanSitemapXml;
        this.bodySnippetMaxLength = Math.max(100, configuredBodySnippetMaxLength);
        this.maxRedirects = Math.max(0, configuredMaxRedirects);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @Override
    public void collect(UUID monitoringRunId) {
        MonitoringRun monitoringRun = monitoringRunRepository.findById(monitoringRunId)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring run not found: " + monitoringRunId));

        Website website = websiteRepository.findById(monitoringRun.getWebsiteId())
                .orElseThrow(() -> new IllegalArgumentException("Website not found: " + monitoringRun.getWebsiteId()));

        validateScannerTarget(website, monitoringRun);

        HttpResponse<String> homepageResponse = scanHomepage(website, monitoringRun);
        String origin = resolveOrigin(homepageResponse.uri(), website);

        String robotsTxtUrl = origin + "/robots.txt";
        String sitemapXmlUrl = origin + "/sitemap.xml";

        if (scanRobotsTxt) {
            scanOptionalResource(website, monitoringRun, robotsTxtUrl, "ROBOTS_TXT");
        } else {
            recordOptionalResourceSkipped(website, monitoringRun, robotsTxtUrl, "ROBOTS_TXT");
        }

        if (scanSitemapXml) {
            scanOptionalResource(website, monitoringRun, sitemapXmlUrl, "SITEMAP_XML");
        } else {
            recordOptionalResourceSkipped(website, monitoringRun, sitemapXmlUrl, "SITEMAP_XML");
        }
    }

    private HttpResponse<String> scanHomepage(Website website, MonitoringRun monitoringRun) {
        String httpsUrl = "https://" + website.getDomain() + "/";

        try {
            HttpResponse<String> response = fetch(httpsUrl);

            record(
                    website,
                    monitoringRun,
                    "HOMEPAGE",
                    "FETCH_OUTCOME",
                    httpsUrl,
                    "SUCCESS"
            );

            recordResponseEvidence(website, monitoringRun, "HOMEPAGE", httpsUrl, response);
            recordHomepageHtmlEvidence(website, monitoringRun, response);

            return response;
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

            String httpUrl = "http://" + website.getDomain() + "/";

            try {
                HttpResponse<String> fallbackResponse = fetch(httpUrl);

                record(
                        website,
                        monitoringRun,
                        "HOMEPAGE",
                        "FETCH_OUTCOME",
                        httpUrl,
                        "SUCCESS"
                );

                recordResponseEvidence(website, monitoringRun, "HOMEPAGE", httpUrl, fallbackResponse);
                recordHomepageHtmlEvidence(website, monitoringRun, fallbackResponse);

                return fallbackResponse;
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
                        safeMessage(fallbackException)
                );

                throw new IllegalStateException(
                        "Homepage fetch failed for both HTTPS and HTTP for domain: " + website.getDomain(),
                        fallbackException
                );
            }
        }
    }

    private void scanOptionalResource(
            Website website,
            MonitoringRun monitoringRun,
            String url,
            String sourceType
    ) {
        try {
            HttpResponse<String> response = fetch(url);

            record(
                    website,
                    monitoringRun,
                    sourceType,
                    "FETCH_OUTCOME",
                    url,
                    "SUCCESS"
            );

            recordResponseEvidence(website, monitoringRun, sourceType, url, response);
            recordBodyFingerprintEvidence(website, monitoringRun, sourceType, url, response.body());
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

    private String cleanUserAgent(String configuredUserAgent) {
        if (configuredUserAgent == null || configuredUserAgent.isBlank()) {
            return DEFAULT_USER_AGENT;
        }

        return configuredUserAgent.trim();
    }

    private HttpResponse<String> fetch(String url) {
        URI currentUri = URI.create(url);

        for (int redirectCount = 0; redirectCount <= maxRedirects; redirectCount++) {
            validateUriTarget(currentUri);

            try {
                HttpRequest request = HttpRequest.newBuilder(currentUri)
                        .timeout(requestTimeout)
                        .header("User-Agent", userAgent)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

                if (!isRedirectStatus(response.statusCode())) {
                    return response;
                }

                Optional<String> location = response.headers().firstValue("location");

                if (location.isEmpty() || location.get().isBlank()) {
                    return response;
                }

                currentUri = resolveRedirectUri(currentUri, location.get());
            } catch (IOException exception) {
                throw new IllegalStateException("HTTP request failed: " + currentUri, exception);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("HTTP request interrupted: " + currentUri, exception);
            } catch (IllegalArgumentException exception) {
                throw new IllegalStateException("Invalid or unsafe URL: " + currentUri, exception);
            }
        }

        throw new IllegalStateException("Maximum redirect limit exceeded: " + url);
    }

    private void recordResponseEvidence(
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String requestedUrl,
            HttpResponse<String> response
    ) {
        record(website, monitoringRun, sourceType, "REQUESTED_URL", requestedUrl, requestedUrl);
        record(website, monitoringRun, sourceType, "HTTP_STATUS", requestedUrl, String.valueOf(response.statusCode()));
        record(website, monitoringRun, sourceType, "FINAL_URL", requestedUrl, response.uri().toString());

        String contentType = firstHeader(response.headers(), "content-type").orElse("MISSING");
        record(website, monitoringRun, sourceType, "CONTENT_TYPE", requestedUrl, contentType);

        recordSecurityHeader(website, monitoringRun, sourceType, requestedUrl, response.headers(), "strict-transport-security", "HEADER_STRICT_TRANSPORT_SECURITY");
        recordSecurityHeader(website, monitoringRun, sourceType, requestedUrl, response.headers(), "content-security-policy", "HEADER_CONTENT_SECURITY_POLICY");
        recordSecurityHeader(website, monitoringRun, sourceType, requestedUrl, response.headers(), "x-frame-options", "HEADER_X_FRAME_OPTIONS");
        recordSecurityHeader(website, monitoringRun, sourceType, requestedUrl, response.headers(), "x-content-type-options", "HEADER_X_CONTENT_TYPE_OPTIONS");
        recordSecurityHeader(website, monitoringRun, sourceType, requestedUrl, response.headers(), "referrer-policy", "HEADER_REFERRER_POLICY");
    }

    private void recordHomepageHtmlEvidence(
            Website website,
            MonitoringRun monitoringRun,
            HttpResponse<String> response
    ) {
        String sourceUrl = response.uri().toString();
        String body = response.body();

        recordBodyFingerprintEvidence(website, monitoringRun, "HOMEPAGE", sourceUrl, body);

        String contentType = firstHeader(response.headers(), "content-type")
                .orElse("")
                .toLowerCase(Locale.ROOT);

        if (!contentType.contains("text/html")) {
            record(website, monitoringRun, "HOMEPAGE", "HTML_PARSE_SKIPPED", sourceUrl, "CONTENT_TYPE_NOT_HTML");
            return;
        }

        String title = extractFirst(TITLE_PATTERN, body).orElse("MISSING");
        String metaDescription = extractMetaDescription(body).orElse("MISSING");
        String canonicalUrl = extractCanonicalUrl(body).orElse("MISSING");

        record(website, monitoringRun, "HOMEPAGE", "PAGE_TITLE", sourceUrl, title);
        record(website, monitoringRun, "HOMEPAGE", "META_DESCRIPTION", sourceUrl, metaDescription);
        record(website, monitoringRun, "HOMEPAGE", "CANONICAL_URL", sourceUrl, canonicalUrl);
    }

    private void recordBodyFingerprintEvidence(
            Website website,
            MonitoringRun monitoringRun,
            String sourceType,
            String sourceUrl,
            String body
    ) {
        String safeBody = body == null ? "" : body;

        record(website, monitoringRun, sourceType, "BODY_LENGTH", sourceUrl, String.valueOf(safeBody.length()));
        record(website, monitoringRun, sourceType, "BODY_SHA256", sourceUrl, sha256(safeBody));

        if (!safeBody.isBlank()) {
            record(
                    website,
                    monitoringRun,
                    sourceType,
                    "BODY_SNIPPET",
                    sourceUrl,
                    truncate(cleanText(safeBody), bodySnippetMaxLength)
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

    private Optional<String> extractMetaDescription(String html) {
        return extractFirst(META_DESCRIPTION_PATTERN_ONE, html)
                .or(() -> extractFirst(META_DESCRIPTION_PATTERN_TWO, html));
    }

    private Optional<String> extractCanonicalUrl(String html) {
        return extractFirst(CANONICAL_PATTERN_ONE, html)
                .or(() -> extractFirst(CANONICAL_PATTERN_TWO, html));
    }

    private Optional<String> extractFirst(Pattern pattern, String html) {
        if (html == null || html.isBlank()) {
            return Optional.empty();
        }

        Matcher matcher = pattern.matcher(html);

        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(cleanText(matcher.group(1)))
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

    private String cleanText(String value) {
        return value
                .replaceAll("(?is)<script[^>]*>.*?</script>", " ")
                .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                .replaceAll("(?is)<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();
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

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();

            for (byte item : hash) {
                result.append(String.format("%02x", item));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", exception);
        }
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