package com.cigabyte.sitesentinel.reporting;

public enum SiteSentinelReportLanguage {

    ENGLISH(
            "ENGLISH",
            "en"
    ),

    TURKISH(
            "TURKISH",
            "tr"
    );

    private final String persistenceValue;

    private final String fileToken;

    SiteSentinelReportLanguage(
            String persistenceValue,
            String fileToken
    ) {
        this.persistenceValue =
                persistenceValue;

        this.fileToken =
                fileToken;
    }

    public String getPersistenceValue() {
        return persistenceValue;
    }

    public String getFileToken() {
        return fileToken;
    }
}