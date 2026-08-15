package com.cigabyte.sitesentinel.engine.collection;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Objects;

final class ScriptAndStyleFilteringReader extends Reader {

    private static final int MAXIMUM_TAG_BUFFER_LENGTH =
            64 * 1024;

    private final Reader delegate;

    private final Deque<Integer> outputBuffer =
            new ArrayDeque<>();

    private String suppressedElementName;

    private boolean closed;

    ScriptAndStyleFilteringReader(
            Reader delegate
    ) {
        this.delegate = Objects.requireNonNull(
                delegate,
                "Source HTML reader is required."
        );
    }

    @Override
    public int read()
            throws IOException {

        ensureOpen();

        if (!outputBuffer.isEmpty()) {
            return outputBuffer.removeFirst();
        }

        if (suppressedElementName != null) {
            consumeSuppressedElement();

            if (!outputBuffer.isEmpty()) {
                return outputBuffer.removeFirst();
            }
        }

        int character =
                delegate.read();

        if (character == -1) {
            return -1;
        }

        if (character != '<') {
            return character;
        }

        String tag =
                readTag();

        TagDescriptor descriptor =
                describeTag(tag);

        if (descriptor
                .isOpeningSuppressedElement()) {

            suppressedElementName =
                    descriptor.elementName();

            return ' ';
        }

        enqueue(tag);

        return outputBuffer.removeFirst();
    }

    @Override
    public int read(
            char[] buffer,
            int offset,
            int length
    ) throws IOException {

        Objects.checkFromIndexSize(
                offset,
                length,
                buffer.length
        );

        if (length == 0) {
            return 0;
        }

        int firstCharacter =
                read();

        if (firstCharacter == -1) {
            return -1;
        }

        buffer[offset] =
                (char) firstCharacter;

        int charactersRead = 1;

        while (charactersRead < length) {
            int character =
                    read();

            if (character == -1) {
                break;
            }

            buffer[offset + charactersRead] =
                    (char) character;

            charactersRead++;
        }

        return charactersRead;
    }

    @Override
    public void close()
            throws IOException {

        if (closed) {
            return;
        }

        closed = true;

        outputBuffer.clear();

        delegate.close();
    }

    private void consumeSuppressedElement()
            throws IOException {

        while (suppressedElementName != null) {
            int character =
                    delegate.read();

            if (character == -1) {
                suppressedElementName = null;
                return;
            }

            if (character != '<') {
                continue;
            }

            String tag =
                    readTag();

            TagDescriptor descriptor =
                    describeTag(tag);

            if (descriptor.isClosingElement(
                    suppressedElementName
            )) {
                suppressedElementName = null;

                outputBuffer.addLast(
                        (int) ' '
                );

                return;
            }
        }
    }

    private String readTag()
            throws IOException {

        StringBuilder tag =
                new StringBuilder();

        tag.append('<');

        boolean singleQuoted = false;
        boolean doubleQuoted = false;

        while (tag.length()
                < MAXIMUM_TAG_BUFFER_LENGTH) {

            int character =
                    delegate.read();

            if (character == -1) {
                break;
            }

            char item =
                    (char) character;

            tag.append(item);

            if (item == '\''
                    && !doubleQuoted) {

                singleQuoted =
                        !singleQuoted;

            } else if (item == '"'
                    && !singleQuoted) {

                doubleQuoted =
                        !doubleQuoted;

            } else if (item == '>'
                    && !singleQuoted
                    && !doubleQuoted) {

                break;
            }
        }

        return tag.toString();
    }

    private TagDescriptor describeTag(
            String tag
    ) {
        int index = 1;

        while (index < tag.length()
                && Character.isWhitespace(
                tag.charAt(index)
        )) {

            index++;
        }

        boolean closing = false;

        if (index < tag.length()
                && tag.charAt(index) == '/') {

            closing = true;
            index++;

            while (index < tag.length()
                    && Character.isWhitespace(
                    tag.charAt(index)
            )) {

                index++;
            }
        }

        int nameStart = index;

        while (index < tag.length()) {
            char character =
                    tag.charAt(index);

            if (!Character.isLetterOrDigit(
                    character
            )
                    && character != '-'
                    && character != ':') {

                break;
            }

            index++;
        }

        if (nameStart == index) {
            return new TagDescriptor(
                    "",
                    closing,
                    false
            );
        }

        String elementName =
                tag.substring(
                                nameStart,
                                index
                        )
                        .toLowerCase(
                                Locale.ROOT
                        );

        boolean selfClosing =
                tag.stripTrailing()
                        .endsWith("/>");

        return new TagDescriptor(
                elementName,
                closing,
                selfClosing
        );
    }

    private void enqueue(
            String value
    ) {
        for (int index = 0;
             index < value.length();
             index++) {

            outputBuffer.addLast(
                    (int) value.charAt(index)
            );
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(
                    "HTML filtering reader has already been closed."
            );
        }
    }

    private record TagDescriptor(
            String elementName,
            boolean closing,
            boolean selfClosing
    ) {

        private boolean
        isOpeningSuppressedElement() {

            return !closing
                    && !selfClosing
                    && (
                    "script".equals(
                            elementName
                    )
                            || "style".equals(
                            elementName
                    )
            );
        }

        private boolean isClosingElement(
                String expectedElementName
        ) {
            return closing
                    && elementName.equals(
                    expectedElementName
            );
        }
    }
}