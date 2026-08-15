package com.cigabyte.sitesentinel.engine.collection;

import org.springframework.stereotype.Component;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Component
public class StreamingHtmlContentExtractor {

    private static final int MAXIMUM_PAGE_TITLE_LENGTH =
            4 * 1024;

    private static final int MAXIMUM_METADATA_VALUE_LENGTH =
            16 * 1024;

    public HtmlContent extract(
            CollectedHttpResponse response,
            Charset sourceCharset,
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

        HtmlExtractionCallback callback =
                new HtmlExtractionCallback(
                        snippetMaxLength
                );

        try (Reader reader =
                     new ScriptAndStyleFilteringReader(
                             response.openBodyReader(
                                     sourceCharset
                             )
                     )) {

            new ParserDelegator().parse(
                    reader,
                    callback,
                    true
            );
        }

        return callback.toResult();
    }

    public record HtmlContent(
            Optional<String> bodySnippet,
            Optional<String> pageTitle,
            Optional<String> metaDescription,
            Optional<String> canonicalUrl
    ) {

        public HtmlContent {
            bodySnippet = requireOptional(
                    bodySnippet,
                    "Body snippet"
            );

            pageTitle = requireOptional(
                    pageTitle,
                    "Page title"
            );

            metaDescription = requireOptional(
                    metaDescription,
                    "Meta description"
            );

            canonicalUrl = requireOptional(
                    canonicalUrl,
                    "Canonical URL"
            );
        }

        private static Optional<String> requireOptional(
                Optional<String> value,
                String fieldName
        ) {
            return Objects.requireNonNull(
                    value,
                    fieldName
                            + " optional value is required."
            );
        }
    }

    private static final class HtmlExtractionCallback
            extends HTMLEditorKit.ParserCallback {

        private final int snippetMaxLength;

        private final StringBuilder bodySnippet =
                new StringBuilder();

        private final StringBuilder currentTitle =
                new StringBuilder();

        private int suppressedTextDepth;

        private boolean insideTitle;

        private boolean snippetWhitespacePending;

        private boolean titleWhitespacePending;

        private String pageTitle;

        private String metaDescription;

        private String canonicalUrl;

        private HtmlExtractionCallback(
                int snippetMaxLength
        ) {
            this.snippetMaxLength =
                    snippetMaxLength;
        }

        @Override
        public void handleStartTag(
                HTML.Tag tag,
                MutableAttributeSet attributes,
                int position
        ) {
            markSnippetBoundary();

            updateSuppressedDepthOnStart(
                    tag
            );

            if (tag == HTML.Tag.TITLE
                    && pageTitle == null) {

                insideTitle = true;

                currentTitle.setLength(0);

                titleWhitespacePending = false;
            }

            inspectMetadataTag(
                    tag,
                    attributes
            );
        }

        @Override
        public void handleSimpleTag(
                HTML.Tag tag,
                MutableAttributeSet attributes,
                int position
        ) {
            markSnippetBoundary();

            inspectMetadataTag(
                    tag,
                    attributes
            );
        }

        @Override
        public void handleEndTag(
                HTML.Tag tag,
                int position
        ) {
            markSnippetBoundary();

            if (tag == HTML.Tag.TITLE
                    && insideTitle) {

                insideTitle = false;

                String normalizedTitle =
                        currentTitle
                                .toString()
                                .trim();

                if (!normalizedTitle.isBlank()) {
                    pageTitle =
                            normalizedTitle;
                }
            }

            updateSuppressedDepthOnEnd(
                    tag
            );
        }

        @Override
        public void handleText(
                char[] data,
                int position
        ) {
            if (suppressedTextDepth > 0) {
                return;
            }

            if (insideTitle) {
                titleWhitespacePending =
                        appendNormalizedText(
                                currentTitle,
                                data,
                                MAXIMUM_PAGE_TITLE_LENGTH,
                                titleWhitespacePending
                        );
            }

            snippetWhitespacePending =
                    appendNormalizedText(
                            bodySnippet,
                            data,
                            snippetMaxLength,
                            snippetWhitespacePending
                    );
        }

        private HtmlContent toResult() {
            return new HtmlContent(
                    optionalText(
                            bodySnippet.toString()
                    ),
                    optionalText(
                            pageTitle
                    ),
                    optionalText(
                            metaDescription
                    ),
                    optionalText(
                            canonicalUrl
                    )
            );
        }

        private void inspectMetadataTag(
                HTML.Tag tag,
                MutableAttributeSet attributes
        ) {
            if (tag == HTML.Tag.META
                    && metaDescription == null) {

                String name =
                        attributeValue(
                                attributes,
                                HTML.Attribute.NAME
                        );

                if ("description"
                        .equalsIgnoreCase(name)) {

                    String candidate =
                            normalizeValue(
                                    attributeValue(
                                            attributes,
                                            HTML.Attribute.CONTENT
                                    ),
                                    MAXIMUM_METADATA_VALUE_LENGTH
                            );

                    if (candidate != null
                            && !candidate.isBlank()) {

                        metaDescription =
                                candidate;
                    }
                }
            }

            if (tag == HTML.Tag.LINK
                    && canonicalUrl == null) {

                String relation =
                        attributeValue(
                                attributes,
                                HTML.Attribute.REL
                        );

                if (containsCanonicalRelation(
                        relation
                )) {

                    String candidate =
                            normalizeValue(
                                    attributeValue(
                                            attributes,
                                            HTML.Attribute.HREF
                                    ),
                                    MAXIMUM_METADATA_VALUE_LENGTH
                            );

                    if (candidate != null
                            && !candidate.isBlank()) {

                        canonicalUrl =
                                candidate;
                    }
                }
            }
        }

        private String attributeValue(
                MutableAttributeSet attributes,
                HTML.Attribute attribute
        ) {
            Object value =
                    attributes.getAttribute(
                            attribute
                    );

            return value == null
                    ? null
                    : value.toString();
        }

        private boolean containsCanonicalRelation(
                String relation
        ) {
            if (relation == null
                    || relation.isBlank()) {

                return false;
            }

            String[] tokens =
                    relation.trim()
                            .toLowerCase(
                                    Locale.ROOT
                            )
                            .split("\\s+");

            for (String token : tokens) {
                if ("canonical".equals(token)) {
                    return true;
                }
            }

            return false;
        }

        private void updateSuppressedDepthOnStart(
                HTML.Tag tag
        ) {
            if (tag == HTML.Tag.SCRIPT
                    || tag == HTML.Tag.STYLE) {

                suppressedTextDepth++;
            }
        }

        private void updateSuppressedDepthOnEnd(
                HTML.Tag tag
        ) {
            if ((tag == HTML.Tag.SCRIPT
                    || tag == HTML.Tag.STYLE)
                    && suppressedTextDepth > 0) {

                suppressedTextDepth--;
            }
        }

        private void markSnippetBoundary() {
            if (!bodySnippet.isEmpty()) {
                snippetWhitespacePending = true;
            }
        }

        private boolean appendNormalizedText(
                StringBuilder destination,
                char[] source,
                int maxLength,
                boolean whitespacePending
        ) {
            if (destination.length()
                    >= maxLength) {

                return whitespacePending;
            }

            boolean pendingWhitespace =
                    whitespacePending;

            for (char character : source) {
                if (Character.isWhitespace(
                        character
                )
                        || Character.isSpaceChar(
                        character
                )) {

                    if (!destination.isEmpty()) {
                        pendingWhitespace = true;
                    }

                    continue;
                }

                if (pendingWhitespace
                        && !destination.isEmpty()
                        && destination.length()
                        < maxLength) {

                    destination.append(' ');
                }

                pendingWhitespace = false;

                if (destination.length()
                        >= maxLength) {

                    break;
                }

                destination.append(
                        character
                );
            }

            return pendingWhitespace;
        }

        private Optional<String> optionalText(
                String value
        ) {
            return Optional.ofNullable(
                            value
                    )
                    .map(String::trim)
                    .filter(
                            item ->
                                    !item.isBlank()
                    );
        }

        private String normalizeValue(
                String value,
                int maximumLength
        ) {
            if (value == null) {
                return null;
            }

            String normalizedValue =
                    value.replaceAll(
                                    "\\s+",
                                    " "
                            )
                            .trim();

            if (normalizedValue.length()
                    <= maximumLength) {

                return normalizedValue;
            }

            return normalizedValue.substring(
                    0,
                    maximumLength
            );
        }
    }
}