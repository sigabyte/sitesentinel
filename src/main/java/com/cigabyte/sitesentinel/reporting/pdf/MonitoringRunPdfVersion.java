package com.cigabyte.sitesentinel.reporting.pdf;

public enum MonitoringRunPdfVersion {

    V1("monitoring-run-pdf-v1");

    private final String value;

    MonitoringRunPdfVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}