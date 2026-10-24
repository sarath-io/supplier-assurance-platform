
package com.supplierassurance.risk.domain;

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static RiskLevel fromScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Risk score must be between 0 and 100."
            );
        }

        if (score <= 20) {
            return LOW;
        }

        if (score <= 50) {
            return MEDIUM;
        }

        if (score <= 75) {
            return HIGH;
        }

        return CRITICAL;
    }
}
