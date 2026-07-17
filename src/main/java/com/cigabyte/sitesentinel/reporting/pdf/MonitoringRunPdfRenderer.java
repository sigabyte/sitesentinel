package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.reporting.MonitoringRunReportView;

public interface MonitoringRunPdfRenderer {

    MonitoringRunPdfVersion getReportVersion();

    byte[] render(
            MonitoringRunReportView reportView
    );
}