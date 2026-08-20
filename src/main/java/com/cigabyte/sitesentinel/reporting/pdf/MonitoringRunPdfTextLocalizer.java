package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.SiteSentinelReportLanguage;

import java.util.Map;
import java.util.Objects;

final class MonitoringRunPdfTextLocalizer {

    private static final Map<String, String>
            TURKISH_TEXT_BY_ENGLISH_TEXT =
            Map.ofEntries(
                    Map.entry(
                            "Report Baseline",
                            "Rapor Temeli"
                    ),
                    Map.entry(
                            "Website",
                            "Web Sitesi"
                    ),
                    Map.entry(
                            "Monitoring Run",
                            "\u0130zleme "
                                    + "\u00C7al\u0131\u015Ft\u0131rmas\u0131"
                    ),
                    Map.entry(
                            "Lifecycle Output Counts",
                            "Ya\u015Fam D\u00F6ng\u00FCs\u00FC "
                                    + "\u00C7\u0131kt\u0131 "
                                    + "Say\u0131lar\u0131"
                    ),
                    Map.entry(
                            "Trust Assessment",
                            "G\u00FCven De\u011Ferlendirmesi"
                    ),
                    Map.entry(
                            "Traceability Summary",
                            "\u0130zlenebilirlik \u00D6zeti"
                    ),
                    Map.entry(
                            "Assessment Comparison",
                            "De\u011Ferlendirme "
                                    + "Kar\u015F\u0131la\u015Ft\u0131rmas\u0131"
                    ),
                    Map.entry(
                            "Findings",
                            "Bulgular"
                    ),
                    Map.entry(
                            "Risks",
                            "Riskler"
                    ),
                    Map.entry(
                            "Advisory Remediation Recommendations",
                            "Dan\u0131\u015Fmanl\u0131k "
                                    + "Ama\u00E7l\u0131 "
                                    + "\u0130yile\u015Ftirme "
                                    + "\u00D6nerileri"
                    ),
                    Map.entry(
                            "Report Boundary",
                            "Rapor S\u0131n\u0131r\u0131"
                    ),
                    Map.entry(
                            "PDF Report Version",
                            "PDF Rapor S\u00FCr\u00FCm\u00FC"
                    ),
                    Map.entry(
                            "Report Status",
                            "Rapor Durumu"
                    ),
                    Map.entry(
                            "Run Status",
                            "\u00C7al\u0131\u015Ft\u0131rma "
                                    + "Durumu"
                    ),
                    Map.entry(
                            "Trigger Type",
                            "Tetikleme T\u00FCr\u00FC"
                    ),
                    Map.entry(
                            "Comparison",
                            "Kar\u015F\u0131la\u015Ft\u0131rma"
                    )
            );

    private final SiteSentinelReportLanguage
            reportLanguage;

    MonitoringRunPdfTextLocalizer(
            SiteSentinelReportLanguage reportLanguage
    ) {
        this.reportLanguage =
                Objects.requireNonNull(
                        reportLanguage,
                        "PDF report language is required."
                );
    }

    String localize(
            String value
    ) {
        if (value == null
                || reportLanguage
                == SiteSentinelReportLanguage.ENGLISH) {

            return value;
        }

        return TURKISH_TEXT_BY_ENGLISH_TEXT
                .getOrDefault(
                        value,
                        value
                );
    }
}