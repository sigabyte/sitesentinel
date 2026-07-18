package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TelegramDocumentUploadRequestTests {

    @Test
    void constructorNormalizesValuesAndProtectsDocumentBytes() {
        byte[] originalBytes =
                "%PDF-test-content"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        TelegramDocumentUploadRequest request =
                new TelegramDocumentUploadRequest(
                        "  test-chat-id  ",
                        "  monitoring-report.pdf  ",
                        "  application/pdf  ",
                        originalBytes,
                        "  SiteSentinel monitoring report  "
                );

        originalBytes[0] = 'X';

        assertEquals(
                "test-chat-id",
                request.getChatId()
        );

        assertEquals(
                "monitoring-report.pdf",
                request.getFileName()
        );

        assertEquals(
                "application/pdf",
                request.getContentType()
        );

        assertEquals(
                "SiteSentinel monitoring report",
                request.getCaption()
        );

        assertEquals(
                '%',
                request.getDocumentBytes()[0]
        );

        byte[] returnedBytes =
                request.getDocumentBytes();

        returnedBytes[1] = 'X';

        assertEquals(
                'P',
                request.getDocumentBytes()[1]
        );
    }

    @Test
    void constructorRejectsMissingRequiredTextValues() {
        byte[] documentBytes =
                "%PDF-test"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        null,
                        "report.pdf",
                        "application/pdf",
                        documentBytes,
                        "caption"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        " ",
                        "application/pdf",
                        documentBytes,
                        "caption"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "report.pdf",
                        null,
                        documentBytes,
                        "caption"
                )
        );
    }

    @Test
    void constructorRejectsUnsafeFileNames() {
        byte[] documentBytes =
                "%PDF-test"
                        .getBytes(
                                StandardCharsets.US_ASCII
                        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "../report.pdf",
                        "application/pdf",
                        documentBytes,
                        "caption"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "folder\\report.pdf",
                        "application/pdf",
                        documentBytes,
                        "caption"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "report\".pdf",
                        "application/pdf",
                        documentBytes,
                        "caption"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "report\r\n.pdf",
                        "application/pdf",
                        documentBytes,
                        "caption"
                )
        );
    }

    @Test
    void constructorRejectsMissingDocumentBytes() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "report.pdf",
                        "application/pdf",
                        null,
                        "caption"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "report.pdf",
                        "application/pdf",
                        new byte[0],
                        "caption"
                )
        );
    }

    @Test
    void constructorRejectsCaptionOverTelegramLimit() {
        String oversizedCaption =
                "a".repeat(1025);

        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramDocumentUploadRequest(
                        "chat-id",
                        "report.pdf",
                        "application/pdf",
                        "%PDF-test"
                                .getBytes(
                                        StandardCharsets.US_ASCII
                                ),
                        oversizedCaption
                )
        );
    }
}