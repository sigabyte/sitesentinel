package com.cigabyte.sitesentinel.reporting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonitoringRunReportViewLanguageTests {

    @Test
    void existingConstructorDefaultsReportLanguageToEnglish() {
        MonitoringRunReportView reportView =
                new MonitoringRunReportView(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(
                SiteSentinelReportLanguage.ENGLISH,
                reportView.getReportLanguage()
        );
    }

    @Test
    void languageAwareConstructorPreservesTurkishReportLanguage() {
        MonitoringRunReportView reportView =
                new MonitoringRunReportView(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        SiteSentinelReportLanguage.TURKISH
                );

        assertEquals(
                SiteSentinelReportLanguage.TURKISH,
                reportView.getReportLanguage()
        );
    }

    @Test
    void turkishReportUsesTurkishReportTitle() {
        MonitoringRunReportView reportView =
                new MonitoringRunReportView(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        SiteSentinelReportLanguage.TURKISH
                );

        assertEquals(
                "SiteSentinel İzleme Raporu",
                reportView.getReportTitle()
        );
    }
}