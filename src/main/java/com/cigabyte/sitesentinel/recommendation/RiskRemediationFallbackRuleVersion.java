package com.cigabyte.sitesentinel.recommendation;

public enum RiskRemediationFallbackRuleVersion {

    V1("risk-remediation-fallback-v1");

    private final String version;

    RiskRemediationFallbackRuleVersion(
            String version
    ) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}