package com.cigabyte.sitesentinel.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.UUID;

@SpringBootTest(
        properties = {
                "sitesentinel.security.username=test-operator",
                "sitesentinel.security.password=test-operator-password"
        }
)
@AutoConfigureMockMvc
class SiteSentinelSecurityContractTests {

    private final MockMvc mockMvc;

    @Autowired
    SiteSentinelSecurityContractTests(
            MockMvc mockMvc
    ) {
        this.mockMvc = mockMvc;
    }

    @Test
    void anonymousDashboardAccessRequiresAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/")
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login"
                        )
                );
    }

    @Test
    void loginPageUsesSiteSentinelSecurityView()
            throws Exception {

        mockMvc.perform(
                        get("/login")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "security/login"
                        )
                );
    }

    @Test
    void configuredOperatorCanAuthenticate()
            throws Exception {

        mockMvc.perform(
                        formLogin()
                                .user(
                                        "test-operator"
                                )
                                .password(
                                        "test-operator-password"
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/"
                        )
                )
                .andExpect(
                        authenticated()
                                .withUsername(
                                        "test-operator"
                                )
                                .withRoles(
                                        "OPERATOR"
                                )
                );
    }

    @Test
    void invalidPasswordDoesNotAuthenticateOperator()
            throws Exception {

        mockMvc.perform(
                        formLogin()
                                .user(
                                        "test-operator"
                                )
                                .password(
                                        "incorrect-password"
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login?error"
                        )
                )
                .andExpect(
                        unauthenticated()
                );
    }

    @Test
    void authenticatedLogoutWithoutCsrfIsRejected()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/logout"
                        )
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void authenticatedLogoutWithCsrfInvalidatesAuthentication()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/logout"
                        )
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                                .with(
                                        csrf()
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login?logout"
                        )
                )
                .andExpect(
                        unauthenticated()
                );
    }

    @Test
    void authenticatedOperatorCanAccessDashboard()
            throws Exception {

        mockMvc.perform(
                        get("/")
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "dashboard/index"
                        )
                )
                .andExpect(
                        authenticated()
                                .withUsername(
                                        "test-operator"
                                )
                                .withRoles(
                                        "OPERATOR"
                                )
                );
    }

    @Test
    void anonymousPdfArtifactDownloadRequiresAuthentication()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID artifactId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts"
                                        + "/{artifactId}/download",
                                websiteId,
                                monitoringRunId,
                                artifactId
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login"
                        )
                )
                .andExpect(
                        unauthenticated()
                );
    }

    @Test
    void anonymousDeliverySettingsAccessRequiresAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/notifications/delivery/settings"
                        )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login"
                        )
                )
                .andExpect(
                        unauthenticated()
                );
    }

    @Test
    void stylesheetRemainsAvailableWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/css/app.css"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        unauthenticated()
                );
    }

    @Test
    void authenticatedPdfGenerationWithoutCsrfIsRejected()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/pdf-artifacts",
                                websiteId,
                                monitoringRunId
                        )
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void authenticatedTelegramHealthCheckWithoutCsrfIsRejected()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/notifications/delivery/settings"
                                        + "/telegram/health-check"
                        )
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void authenticatedReportRetryWithoutCsrfIsRejected()
            throws Exception {

        UUID websiteId =
                UUID.randomUUID();

        UUID monitoringRunId =
                UUID.randomUUID();

        UUID attemptId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/websites/{websiteId}"
                                        + "/monitoring-runs/{runId}"
                                        + "/report/dispatch-attempts"
                                        + "/{attemptId}/retry",
                                websiteId,
                                monitoringRunId,
                                attemptId
                        )
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void authenticatedDashboardProvidesCsrfProtectedLogoutControl()
            throws Exception {

        mockMvc.perform(
                        get("/")
                                .with(
                                        user(
                                                "test-operator"
                                        )
                                                .roles(
                                                        "OPERATOR"
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        content().string(
                                containsString(
                                        "action=\"/logout\""
                                )
                        )
                )
                .andExpect(
                        content().string(
                                containsString(
                                        "name=\"_csrf\""
                                )
                        )
                )
                .andExpect(
                        content().string(
                                containsString(
                                        "Sign out"
                                )
                        )
                );
    }
}