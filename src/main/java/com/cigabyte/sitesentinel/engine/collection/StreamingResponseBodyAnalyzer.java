package com.cigabyte.sitesentinel.engine.collection;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Component
public class StreamingResponseBodyAnalyzer {

    private final StreamingResponseBodyFingerprintCalculator
            fingerprintCalculator;

    private final StreamingHtmlContentExtractor
            htmlContentExtractor;

    public StreamingResponseBodyAnalyzer(
            StreamingResponseBodyFingerprintCalculator
                    fingerprintCalculator,
            StreamingHtmlContentExtractor
                    htmlContentExtractor
    ) {
        this.fingerprintCalculator =
                fingerprintCalculator;

        this.htmlContentExtractor =
                htmlContentExtractor;
    }

    public ResponseBodyAnalysisResult analyze(
            CollectedHttpResponse response,
            Charset sourceCharset,
            String contentType,
            int snippetMaxLength
    ) throws IOException {
        Objects.requireNonNull(
                response,
                "Collected HTTP response is required."
        );

        Objects.requireNonNull(
                sourceCharset,
                "Response body source charset is required."
        );

        if (snippetMaxLength < 1) {
            throw new IllegalArgumentException(
                    "Body snippet maximum length must be positive."
            );
        }

        StreamingResponseBodyFingerprintCalculator
                .Fingerprint fingerprint =
                fingerprintCalculator.calculate(
                        response,
                        sourceCharset
                );

        StreamingHtmlContentExtractor.HtmlContent
                extractedContent =
                htmlContentExtractor.extract(
                        response,
                        sourceCharset,
                        snippetMaxLength
                );

        boolean htmlResponse =
                isHtmlContentType(
                        contentType
                );

        return new ResponseBodyAnalysisResult(
                response.getBodyByteLength(),
                fingerprint.characterLength(),
                fingerprint.sha256(),
                extractedContent.bodySnippet(),
                htmlResponse
                        ? extractedContent.pageTitle()
                        : Optional.empty(),
                htmlResponse
                        ? extractedContent.metaDescription()
                        : Optional.empty(),
                htmlResponse
                        ? extractedContent.canonicalUrl()
                        : Optional.empty()
        );
    }

    private boolean isHtmlContentType(
            String contentType
    ) {
        if (contentType == null
                || contentType.isBlank()) {

            return false;
        }

        String normalizedContentType =
                contentType.toLowerCase(
                        Locale.ROOT
                );

        return normalizedContentType.contains(
                "text/html"
        );
    }
}