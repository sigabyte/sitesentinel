package com.cigabyte.sitesentinel.reporting.pdf;

import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping(
        "/websites/{websiteId}/monitoring-runs/{runId}"
                + "/report/pdf-artifacts"
)
public class MonitoringRunPdfArtifactController {

    private final MonitoringRunPdfArtifactGenerationService
            generationService;

    private final MonitoringRunPdfArtifactService
            artifactService;

    private final MonitoringRunService
            monitoringRunService;

    public MonitoringRunPdfArtifactController(
            MonitoringRunPdfArtifactGenerationService
                    generationService,
            MonitoringRunPdfArtifactService artifactService,
            MonitoringRunService monitoringRunService
    ) {
        this.generationService = generationService;
        this.artifactService = artifactService;
        this.monitoringRunService = monitoringRunService;
    }

    @PostMapping
    public String generate(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            MonitoringRunPdfArtifact artifact =
                    generationService.generate(
                            websiteId,
                            runId
                    );

            redirectAttributes.addFlashAttribute(
                    "pdfArtifactStatus",
                    "SUCCESS"
            );

            redirectAttributes.addFlashAttribute(
                    "pdfArtifactMessage",
                    "PDF artifact generated successfully."
            );

            redirectAttributes.addFlashAttribute(
                    "generatedPdfArtifactId",
                    artifact.getId()
            );
        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "pdfArtifactStatus",
                    "FAILURE"
            );

            redirectAttributes.addFlashAttribute(
                    "pdfArtifactMessage",
                    "PDF artifact generation was not completed. "
                            + "The monitoring run may be incomplete "
                            + "or a PDF artifact may already exist."
            );
        }

        return reportRedirect(
                websiteId,
                runId
        );
    }

    @GetMapping("/{artifactId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable UUID websiteId,
            @PathVariable UUID runId,
            @PathVariable UUID artifactId
    ) {
        MonitoringRunPdfArtifact artifact;

        try {
            monitoringRunService.findByIdAndWebsiteId(
                    runId,
                    websiteId
            );

            artifact =
                    artifactService.findByIdAndMonitoringRunId(
                            artifactId,
                            runId
                    );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Monitoring run PDF artifact not found."
            );
        }

        byte[] artifactBytes =
                artifact.getArtifactBytes();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_PDF
        );

        headers.setContentLength(
                artifactBytes.length
        );

        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                        + artifact.getFileName()
                        + "\""
        );

        headers.set(
                HttpHeaders.CACHE_CONTROL,
                "no-store"
        );

        headers.set(
                "X-Content-Type-Options",
                "nosniff"
        );

        return new ResponseEntity<>(
                artifactBytes,
                headers,
                HttpStatus.OK
        );
    }

    private String reportRedirect(
            UUID websiteId,
            UUID monitoringRunId
    ) {
        return "redirect:/websites/"
                + websiteId
                + "/monitoring-runs/"
                + monitoringRunId
                + "/report";
    }
}