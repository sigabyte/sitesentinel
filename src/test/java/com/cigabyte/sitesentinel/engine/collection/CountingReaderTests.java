package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountingReaderTests {

    @Test
    void singleCharacterReadsIncrementCountAndIgnoreEndOfStream()
            throws Exception {

        try (CountingReader reader =
                     new CountingReader(
                             new StringReader("abc")
                     )) {

            assertEquals(
                    0,
                    reader.getCharacterCount()
            );

            assertEquals(
                    'a',
                    reader.read()
            );

            assertEquals(
                    1,
                    reader.getCharacterCount()
            );

            assertEquals(
                    'b',
                    reader.read()
            );

            assertEquals(
                    'c',
                    reader.read()
            );

            assertEquals(
                    3,
                    reader.getCharacterCount()
            );

            assertEquals(
                    -1,
                    reader.read()
            );

            assertEquals(
                    3,
                    reader.getCharacterCount()
            );
        }
    }

    @Test
    void bufferedReadsCountOnlyCharactersActuallyRead()
            throws Exception {

        try (CountingReader reader =
                     new CountingReader(
                             new StringReader(
                                     "SiteSentinel"
                             )
                     )) {

            char[] buffer =
                    new char[5];

            assertEquals(
                    5,
                    reader.read(
                            buffer,
                            0,
                            buffer.length
                    )
            );

            assertEquals(
                    5,
                    reader.getCharacterCount()
            );

            assertEquals(
                    5,
                    reader.read(
                            buffer,
                            0,
                            buffer.length
                    )
            );

            assertEquals(
                    10,
                    reader.getCharacterCount()
            );

            assertEquals(
                    2,
                    reader.read(
                            buffer,
                            0,
                            buffer.length
                    )
            );

            assertEquals(
                    12,
                    reader.getCharacterCount()
            );

            assertEquals(
                    -1,
                    reader.read(
                            buffer,
                            0,
                            buffer.length
                    )
            );

            assertEquals(
                    12,
                    reader.getCharacterCount()
            );
        }
    }

    @Test
    void skippedCharactersAreIncludedInCount()
            throws Exception {

        try (CountingReader reader =
                     new CountingReader(
                             new StringReader(
                                     "0123456789"
                             )
                     )) {

            assertEquals(
                    4,
                    reader.skip(4)
            );

            assertEquals(
                    4,
                    reader.getCharacterCount()
            );

            assertEquals(
                    '4',
                    reader.read()
            );

            assertEquals(
                    5,
                    reader.getCharacterCount()
            );

            assertEquals(
                    5,
                    reader.skip(100)
            );

            assertEquals(
                    10,
                    reader.getCharacterCount()
            );

            assertEquals(
                    -1,
                    reader.read()
            );

            assertEquals(
                    10,
                    reader.getCharacterCount()
            );
        }
    }

    @Test
    void mixedOperationsCountCompleteConsumedContent()
            throws Exception {

        String content =
                "SiteSentinel streaming response";

        try (CountingReader reader =
                     new CountingReader(
                             new StringReader(content)
                     )) {

            char[] firstBuffer =
                    new char[4];

            assertEquals(
                    4,
                    reader.read(
                            firstBuffer,
                            0,
                            firstBuffer.length
                    )
            );

            assertEquals(
                    3,
                    reader.skip(3)
            );

            while (reader.read() != -1) {
                // Consume the remaining characters.
            }

            assertEquals(
                    content.length(),
                    reader.getCharacterCount()
            );
        }
    }
}