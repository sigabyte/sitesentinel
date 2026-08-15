package com.cigabyte.sitesentinel.engine.collection;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

final class CountingReader extends FilterReader {

    private long characterCount;

    CountingReader(
            Reader reader
    ) {
        super(reader);
    }

    long getCharacterCount() {
        return characterCount;
    }

    @Override
    public int read()
            throws IOException {

        int character = super.read();

        if (character != -1) {
            incrementCharacterCount(1);
        }

        return character;
    }

    @Override
    public int read(
            char[] buffer,
            int offset,
            int length
    ) throws IOException {

        int charactersRead =
                super.read(
                        buffer,
                        offset,
                        length
                );

        if (charactersRead > 0) {
            incrementCharacterCount(
                    charactersRead
            );
        }

        return charactersRead;
    }

    @Override
    public long skip(
            long characterCountToSkip
    ) throws IOException {

        long skippedCharacters =
                super.skip(
                        characterCountToSkip
                );

        if (skippedCharacters > 0) {
            incrementCharacterCount(
                    skippedCharacters
            );
        }

        return skippedCharacters;
    }

    private void incrementCharacterCount(
            long increment
    ) {
        characterCount =
                Math.addExact(
                        characterCount,
                        increment
                );
    }
}