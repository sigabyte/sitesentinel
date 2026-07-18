package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelegramMultipartBodyTests {

    @Test
    void fromBuildsExpectedMultipartFieldsAndPreservesBinaryDocument() {
        byte[] documentBytes =
                new byte[]{
                        '%',
                        'P',
                        'D',
                        'F',
                        0,
                        (byte) 0xFF,
                        10
                };

        TelegramDocumentUploadRequest request =
                new TelegramDocumentUploadRequest(
                        "test-chat-id",
                        "monitoring-report.pdf",
                        "application/pdf",
                        documentBytes,
                        "Monitoring run report"
                );

        TelegramMultipartBody multipartBody =
                TelegramMultipartBody.from(
                        request
                );

        byte[] bodyBytes =
                multipartBody.getBodyBytes();

        String bodyText =
                new String(
                        bodyBytes,
                        StandardCharsets.ISO_8859_1
                );

        assertEquals(
                "multipart/form-data; boundary="
                        + multipartBody.getBoundary(),
                multipartBody.getContentTypeHeaderValue()
        );

        assertTrue(
                bodyText.contains(
                        "Content-Disposition: form-data; "
                                + "name=\"chat_id\"\r\n\r\n"
                                + "test-chat-id\r\n"
                )
        );

        assertTrue(
                bodyText.contains(
                        "Content-Disposition: form-data; "
                                + "name=\"caption\"\r\n\r\n"
                                + "Monitoring run report\r\n"
                )
        );

        assertTrue(
                bodyText.contains(
                        "Content-Disposition: form-data; "
                                + "name=\"document\"; "
                                + "filename=\"monitoring-report.pdf\"\r\n"
                                + "Content-Type: application/pdf\r\n\r\n"
                )
        );

        assertTrue(
                containsSubsequence(
                        bodyBytes,
                        documentBytes
                )
        );

        assertTrue(
                bodyText.endsWith(
                        "--"
                                + multipartBody.getBoundary()
                                + "--\r\n"
                )
        );
    }

    @Test
    void fromOmitsCaptionPartWhenCaptionIsBlank() {
        TelegramDocumentUploadRequest request =
                new TelegramDocumentUploadRequest(
                        "test-chat-id",
                        "monitoring-report.pdf",
                        "application/pdf",
                        "%PDF".getBytes(
                                StandardCharsets.US_ASCII
                        ),
                        ""
                );

        TelegramMultipartBody multipartBody =
                TelegramMultipartBody.from(
                        request
                );

        String bodyText =
                new String(
                        multipartBody.getBodyBytes(),
                        StandardCharsets.ISO_8859_1
                );

        assertFalse(
                bodyText.contains(
                        "name=\"caption\""
                )
        );
    }

    @Test
    void getBodyBytesReturnsDefensiveCopy() {
        TelegramMultipartBody multipartBody =
                TelegramMultipartBody.from(
                        new TelegramDocumentUploadRequest(
                                "test-chat-id",
                                "monitoring-report.pdf",
                                "application/pdf",
                                "%PDF".getBytes(
                                        StandardCharsets.US_ASCII
                                ),
                                "caption"
                        )
                );

        byte[] originalBody =
                multipartBody.getBodyBytes();

        byte[] returnedBody =
                multipartBody.getBodyBytes();

        returnedBody[0] = 'X';

        assertArrayEquals(
                originalBody,
                multipartBody.getBodyBytes()
        );
    }

    private boolean containsSubsequence(
            byte[] source,
            byte[] target
    ) {
        if (target.length == 0) {
            return true;
        }

        for (int sourceIndex = 0;
             sourceIndex <= source.length - target.length;
             sourceIndex++) {

            boolean matches = true;

            for (int targetIndex = 0;
                 targetIndex < target.length;
                 targetIndex++) {

                if (source[sourceIndex + targetIndex]
                        != target[targetIndex]) {

                    matches = false;
                    break;
                }
            }

            if (matches) {
                return true;
            }
        }

        return false;
    }
}