package com.cigabyte.sitesentinel.security;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteSentinelSecurityPropertiesTests {

    private static ValidatorFactory validatorFactory;

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory =
                Validation.buildDefaultValidatorFactory();

        validator =
                validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void acceptsConfiguredOperatorCredentials() {
        SiteSentinelSecurityProperties properties =
                new SiteSentinelSecurityProperties(
                        "production-operator",
                        "controlled-password"
                );

        Set<ConstraintViolation<SiteSentinelSecurityProperties>>
                violations =
                validator.validate(
                        properties
                );

        assertTrue(
                violations.isEmpty()
        );

        assertEquals(
                "production-operator",
                properties.getUsername()
        );

        assertEquals(
                "controlled-password",
                properties.getPassword()
        );
    }

    @Test
    void rejectsBlankOperatorUsername() {
        SiteSentinelSecurityProperties properties =
                new SiteSentinelSecurityProperties(
                        " ",
                        "controlled-password"
                );

        Set<ConstraintViolation<SiteSentinelSecurityProperties>>
                violations =
                validator.validate(
                        properties
                );

        assertTrue(
                hasViolationForProperty(
                        violations,
                        "username"
                )
        );
    }

    @Test
    void rejectsBlankOperatorPassword() {
        SiteSentinelSecurityProperties properties =
                new SiteSentinelSecurityProperties(
                        "production-operator",
                        " "
                );

        Set<ConstraintViolation<SiteSentinelSecurityProperties>>
                violations =
                validator.validate(
                        properties
                );

        assertTrue(
                hasViolationForProperty(
                        violations,
                        "password"
                )
        );
    }

    private boolean hasViolationForProperty(
            Set<ConstraintViolation<SiteSentinelSecurityProperties>>
                    violations,
            String propertyName
    ) {
        return violations.stream()
                .anyMatch(
                        violation ->
                                propertyName.equals(
                                        violation
                                                .getPropertyPath()
                                                .toString()
                                )
                );
    }
}