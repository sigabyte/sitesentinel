package com.cigabyte.sitesentinel.notification.delivery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TelegramBotApiResponseTests {

    @Test
    void indicatesSuccessfulTelegramResponseAcceptsCompactJson() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {"ok":true,"result":{"id":12345}}
                        """
                );

        assertTrue(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseAcceptsPrettyJson() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "id": 12345
                          }
                        }
                        """
                );

        assertTrue(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsOkFalse() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": false,
                          "description": "Operation failed"
                        }
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsStringTrue() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": "true"
                        }
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsMissingTopLevelOk() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "result": {
                            "ok": true,
                            "id": 12345
                          }
                        }
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsEmbeddedTextMarker() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "description":
                          "Embedded marker: \\"ok\\":true"
                        }
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsMalformedJson() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsEmptyBody() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        ""
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsNullBody() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        null
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsArrayRoot() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        [
                          {
                            "ok": true
                          }
                        ]
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void indicatesSuccessfulTelegramResponseRejectsSuccessfulBodyWithNon2xxStatus() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        500,
                        """
                        {
                          "ok": true
                        }
                        """
                );

        assertFalse(
                response.indicatesSuccessfulTelegramResponse()
        );
    }

    @Test
    void getTelegramMessageIdExtractsNumericMessageIdFromSuccessfulResponse() {
        TelegramBotApiResponse response =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": 2468
                          }
                        }
                        """
                );

        assertEquals(
                Long.valueOf(2468L),
                response.getTelegramMessageId()
        );
    }

    @Test
    void getTelegramMessageIdReturnsNullWhenMessageIdIsMissingOrInvalid() {
        TelegramBotApiResponse missingMessageIdResponse =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "document": {
                              "file_id": "test-file-id"
                            }
                          }
                        }
                        """
                );

        TelegramBotApiResponse stringMessageIdResponse =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": "2468"
                          }
                        }
                        """
                );

        TelegramBotApiResponse missingResultResponse =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": true
                        }
                        """
                );

        assertNull(
                missingMessageIdResponse.getTelegramMessageId()
        );

        assertNull(
                stringMessageIdResponse.getTelegramMessageId()
        );

        assertNull(
                missingResultResponse.getTelegramMessageId()
        );
    }

    @Test
    void getTelegramMessageIdReturnsNullForUnsuccessfulTelegramResponse() {
        TelegramBotApiResponse telegramFailureResponse =
                new TelegramBotApiResponse(
                        200,
                        """
                        {
                          "ok": false,
                          "error_code": 400,
                          "description": "Bad Request"
                        }
                        """
                );

        TelegramBotApiResponse httpFailureResponse =
                new TelegramBotApiResponse(
                        500,
                        """
                        {
                          "ok": true,
                          "result": {
                            "message_id": 2468
                          }
                        }
                        """
                );

        assertNull(
                telegramFailureResponse.getTelegramMessageId()
        );

        assertNull(
                httpFailureResponse.getTelegramMessageId()
        );
    }
}