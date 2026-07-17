package com.cigabyte.sitesentinel.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record RiskRemediationAiOutput(
        String schemaVersion,
        String title,
        String summary,
        List<String> remediationSteps,
        List<String> verificationSteps,
        Boolean advisory
) {

    public RiskRemediationAiOutput {
        remediationSteps =
                immutableCopyAllowingNulls(
                        remediationSteps
                );

        verificationSteps =
                immutableCopyAllowingNulls(
                        verificationSteps
                );
    }

    private static <T> List<T> immutableCopyAllowingNulls(
            List<T> values
    ) {
        if (values == null) {
            return null;
        }

        return Collections.unmodifiableList(
                new ArrayList<>(values)
        );
    }
}