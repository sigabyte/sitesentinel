package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptAndStyleFilteringReaderTests {

    @Test
    void normalHtmlIsPreservedWithoutModification()
            throws Exception {

        String html =
                "<html><head><title>SiteSentinel</title></head>"
                        + "<body><p>Security monitoring</p></body></html>";

        try (ScriptAndStyleFilteringReader reader =
                     new ScriptAndStyleFilteringReader(
                             new StringReader(html)
                     )) {

            assertEquals(
                    html,
                    readFully(reader)
            );
        }
    }

    @Test
    void scriptAndStyleElementsAreRemovedFromStream()
            throws Exception {

        String html =
                "Before"
                        + "<script>window.secret = 'hidden';</script>"
                        + "Middle"
                        + "<style>.hidden { color: red; }</style>"
                        + "After";

        try (ScriptAndStyleFilteringReader reader =
                     new ScriptAndStyleFilteringReader(
                             new StringReader(html)
                     )) {

            String filteredContent =
                    readFully(reader);

            assertEquals(
                    "Before Middle After",
                    normalizeWhitespace(
                            filteredContent
                    )
            );

            assertTrue(
                    !filteredContent.contains(
                            "window.secret"
                    )
            );

            assertTrue(
                    !filteredContent.contains(
                            "color: red"
                    )
            );

            assertTrue(
                    !filteredContent
                            .toLowerCase()
                            .contains("<script")
            );

            assertTrue(
                    !filteredContent
                            .toLowerCase()
                            .contains("<style")
            );
        }
    }

    @Test
    void tagRecognitionIsCaseInsensitiveAndHandlesQuotedGreaterThan()
            throws Exception {

        String html =
                "Visible"
                        + "<ScRiPt data-expression=\"1 > 0\">"
                        + "hiddenScriptValue"
                        + "</sCrIpT>"
                        + "<STYLE media='screen > mobile'>"
                        + "hiddenStyleValue"
                        + "</StYlE>"
                        + "Content";

        try (ScriptAndStyleFilteringReader reader =
                     new ScriptAndStyleFilteringReader(
                             new StringReader(html)
                     )) {

            String filteredContent =
                    readFully(reader);

            assertEquals(
                    "Visible Content",
                    normalizeWhitespace(
                            filteredContent
                    )
            );

            assertTrue(
                    !filteredContent.contains(
                            "hiddenScriptValue"
                    )
            );

            assertTrue(
                    !filteredContent.contains(
                            "hiddenStyleValue"
                    )
            );
        }
    }

    @Test
    void bufferedReadsPreserveAllContentAroundSuppressedElement()
            throws Exception {

        String prefix =
                "A".repeat(100_000);

        String suffix =
                "B".repeat(100_000);

        String html =
                prefix
                        + "<script>"
                        + "ignored".repeat(10_000)
                        + "</script>"
                        + suffix;

        try (ScriptAndStyleFilteringReader reader =
                     new ScriptAndStyleFilteringReader(
                             new StringReader(html)
                     )) {

            String filteredContent =
                    readFully(reader);

            assertEquals(
                    prefix + "  " + suffix,
                    filteredContent
            );

            assertEquals(
                    prefix.length()
                            + 2
                            + suffix.length(),
                    filteredContent.length()
            );

            assertTrue(
                    filteredContent.startsWith(
                            prefix
                    )
            );

            assertTrue(
                    filteredContent.endsWith(
                            suffix
                    )
            );
        }
    }

    @Test
    void unterminatedSuppressedElementIsConsumedSafelyUntilEndOfStream()
            throws Exception {

        String html =
                "Visible content"
                        + "<script>"
                        + "unterminated hidden content";

        try (ScriptAndStyleFilteringReader reader =
                     new ScriptAndStyleFilteringReader(
                             new StringReader(html)
                     )) {

            String filteredContent =
                    readFully(reader);

            assertEquals(
                    "Visible content",
                    filteredContent.trim()
            );

            assertTrue(
                    !filteredContent.contains(
                            "unterminated hidden content"
                    )
            );
        }
    }

    @Test
    void closingFilterClosesDelegateAndRejectsFurtherReads()
            throws Exception {

        TrackingReader delegate =
                new TrackingReader(
                        "<p>SiteSentinel</p>"
                );

        ScriptAndStyleFilteringReader reader =
                new ScriptAndStyleFilteringReader(
                        delegate
                );

        assertEquals(
                '<',
                reader.read()
        );

        reader.close();

        assertTrue(
                delegate.isClosed()
        );

        assertThrows(
                IllegalStateException.class,
                reader::read
        );

        reader.close();
    }

    private String readFully(
            Reader reader
    ) throws Exception {

        StringBuilder content =
                new StringBuilder();

        char[] buffer =
                new char[4096];

        int charactersRead;

        while ((charactersRead =
                reader.read(buffer)) != -1) {

            content.append(
                    buffer,
                    0,
                    charactersRead
            );
        }

        return content.toString();
    }

    private String normalizeWhitespace(
            String value
    ) {
        return value
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }

    private static final class TrackingReader
            extends StringReader {

        private boolean closed;

        private TrackingReader(
                String content
        ) {
            super(content);
        }

        @Override
        public void close() {
            closed = true;
            super.close();
        }

        private boolean isClosed() {
            return closed;
        }
    }
}