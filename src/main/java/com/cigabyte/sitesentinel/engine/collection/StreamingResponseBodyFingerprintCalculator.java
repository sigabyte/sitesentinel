package com.cigabyte.sitesentinel.engine.collection;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;

@Component
public class StreamingResponseBodyFingerprintCalculator {

    private static final int CHARACTER_BUFFER_SIZE =
            8 * 1024;

    public Fingerprint calculate(
            CollectedHttpResponse response,
            Charset sourceCharset
    ) throws IOException {
        Objects.requireNonNull(
                response,
                "Collected HTTP response is required."
        );

        Objects.requireNonNull(
                sourceCharset,
                "Response body source charset is required."
        );

        MessageDigest messageDigest =
                createSha256Digest();

        long characterLength;

        try (CountingReader reader =
                     new CountingReader(
                             response.openBodyReader(
                                     sourceCharset
                             )
                     );
             DigestOutputStream digestOutputStream =
                     new DigestOutputStream(
                             OutputStream.nullOutputStream(),
                             messageDigest
                     );
             Writer digestWriter =
                     new OutputStreamWriter(
                             digestOutputStream,
                             StandardCharsets.UTF_8
                     )) {

            char[] buffer =
                    new char[CHARACTER_BUFFER_SIZE];

            int charactersRead;

            while ((charactersRead =
                    reader.read(buffer)) != -1) {

                digestWriter.write(
                        buffer,
                        0,
                        charactersRead
                );
            }

            digestWriter.flush();

            characterLength =
                    reader.getCharacterCount();
        }

        return new Fingerprint(
                characterLength,
                toHex(
                        messageDigest.digest()
                )
        );
    }

    private MessageDigest createSha256Digest() {
        try {
            return MessageDigest.getInstance(
                    "SHA-256"
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }

    private String toHex(
            byte[] hash
    ) {
        StringBuilder result =
                new StringBuilder(
                        hash.length * 2
                );

        for (byte item : hash) {
            result.append(
                    String.format(
                            Locale.ROOT,
                            "%02x",
                            item
                    )
            );
        }

        return result.toString();
    }

    public record Fingerprint(
            long characterLength,
            String sha256
    ) {

        public Fingerprint {
            if (characterLength < 0) {
                throw new IllegalArgumentException(
                        "Response body character length "
                                + "cannot be negative."
                );
            }

            Objects.requireNonNull(
                    sha256,
                    "Response body SHA-256 is required."
            );

            String normalizedSha256 =
                    sha256.trim()
                            .toLowerCase(
                                    Locale.ROOT
                            );

            if (!normalizedSha256.matches(
                    "[0-9a-f]{64}"
            )) {
                throw new IllegalArgumentException(
                        "Response body SHA-256 must contain "
                                + "64 hexadecimal characters."
                );
            }

            sha256 = normalizedSha256;
        }
    }
}