package com.cigabyte.sitesentinel.recommendation;

public enum RiskRemediationPromptVersion {

    V1(
            "risk-remediation-v1",
            "risk-remediation-output-v1"
    );

    private final String promptVersion;
    private final String outputSchemaVersion;

    RiskRemediationPromptVersion(
            String promptVersion,
            String outputSchemaVersion
    ) {
        this.promptVersion = promptVersion;
        this.outputSchemaVersion = outputSchemaVersion;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String getOutputSchemaVersion() {
        return outputSchemaVersion;
    }
}