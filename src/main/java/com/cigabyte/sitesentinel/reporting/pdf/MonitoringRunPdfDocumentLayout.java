package com.cigabyte.sitesentinel.reporting.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class MonitoringRunPdfDocumentLayout
        implements AutoCloseable {

    private static final PDRectangle PAGE_SIZE =
            PDRectangle.A4;

    private static final float LEFT_MARGIN = 54.0f;
    private static final float RIGHT_MARGIN = 54.0f;
    private static final float TOP_MARGIN = 54.0f;
    private static final float BOTTOM_MARGIN = 54.0f;

    private static final float FOOTER_Y = 28.0f;

    private static final float CONTENT_WIDTH =
            PAGE_SIZE.getWidth()
                    - LEFT_MARGIN
                    - RIGHT_MARGIN;

    private static final float TITLE_FONT_SIZE = 18.0f;
    private static final float TITLE_LINE_HEIGHT = 23.0f;

    private static final float SECTION_FONT_SIZE = 14.0f;
    private static final float SECTION_LINE_HEIGHT = 18.0f;

    private static final float SUBSECTION_FONT_SIZE = 11.0f;
    private static final float SUBSECTION_LINE_HEIGHT = 15.0f;

    private static final float BODY_FONT_SIZE = 10.0f;
    private static final float BODY_LINE_HEIGHT = 14.0f;

    private static final float FOOTER_FONT_SIZE = 8.0f;

    private final PDDocument document;

    private final PDFont regularFont;

    private final PDFont boldFont;

    private PDPageContentStream contentStream;

    private float cursorY;

    private int pageNumber;

    private boolean closed;

    MonitoringRunPdfDocumentLayout(
            PDDocument document
    ) throws IOException {
        this.document = Objects.requireNonNull(
                document,
                "PDF document is required."
        );

        this.regularFont = new PDType1Font(
                Standard14Fonts.FontName.HELVETICA
        );

        this.boldFont = new PDType1Font(
                Standard14Fonts.FontName.HELVETICA_BOLD
        );

        startNewPage();
    }

    void writeDocumentTitle(
            String value
    ) throws IOException {
        ensureOpen();

        ensureSpace(
                TITLE_LINE_HEIGHT + 16.0f
        );

        writeWrappedText(
                value,
                boldFont,
                TITLE_FONT_SIZE,
                TITLE_LINE_HEIGHT
        );

        writeSpacer(10.0f);
        writeDivider();
        writeSpacer(8.0f);
    }

    void writeSectionHeading(
            String value
    ) throws IOException {
        ensureOpen();

        ensureSpace(
                SECTION_LINE_HEIGHT + 24.0f
        );

        if (!isAtTopOfPage()) {
            writeSpacer(8.0f);
        }

        writeWrappedText(
                value,
                boldFont,
                SECTION_FONT_SIZE,
                SECTION_LINE_HEIGHT
        );

        writeSpacer(4.0f);
        writeDivider();
        writeSpacer(6.0f);
    }

    void writeSubsectionHeading(
            String value
    ) throws IOException {
        ensureOpen();

        ensureSpace(
                SUBSECTION_LINE_HEIGHT + 8.0f
        );

        writeWrappedText(
                value,
                boldFont,
                SUBSECTION_FONT_SIZE,
                SUBSECTION_LINE_HEIGHT
        );

        writeSpacer(3.0f);
    }

    void writeParagraph(
            String value
    ) throws IOException {
        ensureOpen();

        writeWrappedText(
                value,
                regularFont,
                BODY_FONT_SIZE,
                BODY_LINE_HEIGHT
        );

        writeSpacer(6.0f);
    }

    void writeKeyValue(
            String label,
            Object value
    ) throws IOException {
        ensureOpen();

        String requiredLabel =
                normalizeRequiredLabel(label);

        String displayValue =
                value == null
                        ? "Not available"
                        : value.toString();

        writeWrappedText(
                requiredLabel
                        + ": "
                        + displayValue,
                regularFont,
                BODY_FONT_SIZE,
                BODY_LINE_HEIGHT
        );
    }

    void writeBullet(
            String value
    ) throws IOException {
        ensureOpen();

        writeWrappedText(
                "- " + normalizeDisplayText(value),
                regularFont,
                BODY_FONT_SIZE,
                BODY_LINE_HEIGHT
        );
    }

    void writeSpacer(
            float height
    ) throws IOException {
        ensureOpen();

        if (height <= 0.0f) {
            return;
        }

        ensureSpace(height);
        cursorY -= height;
    }

    void writeDivider() throws IOException {
        ensureOpen();

        ensureSpace(8.0f);

        contentStream.setLineWidth(0.5f);

        contentStream.moveTo(
                LEFT_MARGIN,
                cursorY
        );

        contentStream.lineTo(
                PAGE_SIZE.getWidth()
                        - RIGHT_MARGIN,
                cursorY
        );

        contentStream.stroke();

        cursorY -= 4.0f;
    }

    int getPageNumber() {
        return pageNumber;
    }

    private void writeWrappedText(
            String value,
            PDFont font,
            float fontSize,
            float lineHeight
    ) throws IOException {
        List<String> lines =
                wrapText(
                        value,
                        font,
                        fontSize
                );

        for (String line : lines) {
            ensureSpace(lineHeight);

            if (!line.isEmpty()) {
                writeTextLine(
                        line,
                        font,
                        fontSize,
                        LEFT_MARGIN,
                        cursorY
                );
            }

            cursorY -= lineHeight;
        }
    }

    private List<String> wrapText(
            String value,
            PDFont font,
            float fontSize
    ) throws IOException {
        String safeText =
                sanitizeForFont(
                        font,
                        normalizeDisplayText(value)
                );

        String[] paragraphs =
                safeText.split(
                        "\n",
                        -1
                );

        List<String> lines =
                new ArrayList<>();

        for (String paragraph : paragraphs) {
            String normalizedParagraph =
                    paragraph.trim();

            if (normalizedParagraph.isEmpty()) {
                lines.add("");
                continue;
            }

            List<String> words =
                    List.of(
                            normalizedParagraph.split(
                                    "\\s+"
                            )
                    );

            StringBuilder currentLine =
                    new StringBuilder();

            for (String word : words) {
                List<String> wordParts =
                        splitLongWord(
                                word,
                                font,
                                fontSize
                        );

                for (String wordPart : wordParts) {
                    String candidateLine =
                            currentLine.isEmpty()
                                    ? wordPart
                                    : currentLine
                                    + " "
                                    + wordPart;

                    if (fitsContentWidth(
                            candidateLine,
                            font,
                            fontSize
                    )) {
                        currentLine.setLength(0);
                        currentLine.append(
                                candidateLine
                        );

                        continue;
                    }

                    if (!currentLine.isEmpty()) {
                        lines.add(
                                currentLine.toString()
                        );

                        currentLine.setLength(0);
                    }

                    currentLine.append(wordPart);
                }
            }

            if (!currentLine.isEmpty()) {
                lines.add(
                        currentLine.toString()
                );
            }
        }

        return List.copyOf(lines);
    }

    private List<String> splitLongWord(
            String word,
            PDFont font,
            float fontSize
    ) throws IOException {
        if (fitsContentWidth(
                word,
                font,
                fontSize
        )) {
            return List.of(word);
        }

        List<String> parts =
                new ArrayList<>();

        StringBuilder currentPart =
                new StringBuilder();

        int offset = 0;

        while (offset < word.length()) {
            int codePoint =
                    word.codePointAt(offset);

            String character =
                    new String(
                            Character.toChars(
                                    codePoint
                            )
                    );

            String candidatePart =
                    currentPart
                            + character;

            if (!currentPart.isEmpty()
                    && !fitsContentWidth(
                    candidatePart,
                    font,
                    fontSize
            )) {
                parts.add(
                        currentPart.toString()
                );

                currentPart.setLength(0);
            }

            currentPart.append(character);

            offset += Character.charCount(
                    codePoint
            );
        }

        if (!currentPart.isEmpty()) {
            parts.add(
                    currentPart.toString()
            );
        }

        return List.copyOf(parts);
    }

    private boolean fitsContentWidth(
            String value,
            PDFont font,
            float fontSize
    ) throws IOException {
        float width =
                font.getStringWidth(value)
                        / 1000.0f
                        * fontSize;

        return width <= CONTENT_WIDTH;
    }

    private String sanitizeForFont(
            PDFont font,
            String value
    ) {
        String normalized =
                normalizeCommonPunctuation(value);

        StringBuilder safeValue =
                new StringBuilder();

        int offset = 0;

        while (offset < normalized.length()) {
            int codePoint =
                    normalized.codePointAt(offset);

            if (codePoint == '\n') {
                safeValue.append('\n');

                offset += Character.charCount(
                        codePoint
                );

                continue;
            }

            if (Character.isWhitespace(codePoint)) {
                safeValue.append(' ');

                offset += Character.charCount(
                        codePoint
                );

                continue;
            }

            if (Character.isISOControl(codePoint)) {
                offset += Character.charCount(
                        codePoint
                );

                continue;
            }

            String character =
                    new String(
                            Character.toChars(
                                    codePoint
                            )
                    );

            try {
                font.encode(character);
                safeValue.append(character);
            } catch (
                    IOException
                    | IllegalArgumentException exception
            ) {
                safeValue.append('?');
            }

            offset += Character.charCount(
                    codePoint
            );
        }

        return safeValue.toString();
    }

    private String normalizeCommonPunctuation(
            String value
    ) {
        return value
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u00A0', ' ')
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201C', '"')
                .replace('\u201D', '"')
                .replace("\u2026", "...")
                .replace('\u2022', '-');
    }

    private String normalizeDisplayText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return "Not available";
        }

        return value.strip();
    }

    private String normalizeRequiredLabel(
            String value
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "PDF field label is required."
            );
        }

        return value.strip();
    }

    private void ensureSpace(
            float requiredHeight
    ) throws IOException {
        float minimumY =
                BOTTOM_MARGIN;

        if (cursorY - requiredHeight
                >= minimumY) {
            return;
        }

        closeCurrentPage();
        startNewPage();
    }

    private boolean isAtTopOfPage() {
        float pageTop =
                PAGE_SIZE.getHeight()
                        - TOP_MARGIN;

        return Math.abs(
                cursorY - pageTop
        ) < 0.01f;
    }

    private void startNewPage()
            throws IOException {
        PDPage page =
                new PDPage(PAGE_SIZE);

        document.addPage(page);

        contentStream =
                new PDPageContentStream(
                        document,
                        page
                );

        pageNumber++;

        cursorY =
                PAGE_SIZE.getHeight()
                        - TOP_MARGIN;
    }

    private void closeCurrentPage()
            throws IOException {
        if (contentStream == null) {
            return;
        }

        writeFooter();
        contentStream.close();
        contentStream = null;
    }

    private void writeFooter()
            throws IOException {
        String footerText =
                "SiteSentinel | Page "
                        + pageNumber;

        writeTextLine(
                footerText,
                regularFont,
                FOOTER_FONT_SIZE,
                LEFT_MARGIN,
                FOOTER_Y
        );
    }

    private void writeTextLine(
            String value,
            PDFont font,
            float fontSize,
            float x,
            float y
    ) throws IOException {
        contentStream.beginText();

        contentStream.setFont(
                font,
                fontSize
        );

        contentStream.newLineAtOffset(
                x,
                y
        );

        contentStream.showText(value);
        contentStream.endText();
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(
                    "PDF document layout is already closed."
            );
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        closeCurrentPage();
        closed = true;
    }
}