package com.cigabyte.sitesentinel.reporting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SiteSentinelReportLanguageTests {

    @Test
    void englishUsesStablePersistenceAndFileTokens() {
        SiteSentinelReportLanguage language =
                SiteSentinelReportLanguage.ENGLISH;

        assertEquals(
                "ENGLISH",
                language.getPersistenceValue()
        );

        assertEquals(
                "en",
                language.getFileToken()
        );
    }

    @Test
    void turkishUsesStablePersistenceAndFileTokens() {
        SiteSentinelReportLanguage language =
                SiteSentinelReportLanguage.TURKISH;

        assertEquals(
                "TURKISH",
                language.getPersistenceValue()
        );

        assertEquals(
                "tr",
                language.getFileToken()
        );
    }
}