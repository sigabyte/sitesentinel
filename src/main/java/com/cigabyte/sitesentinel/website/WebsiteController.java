package com.cigabyte.sitesentinel.website;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;

import java.util.UUID;
import com.cigabyte.sitesentinel.evidence.EvidenceService;
import com.cigabyte.sitesentinel.finding.FindingService;
import com.cigabyte.sitesentinel.risk.RiskService;
import com.cigabyte.sitesentinel.trust.TrustAssessmentService;

@Controller
@RequestMapping("/websites")
public class WebsiteController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;
    private final EvidenceService evidenceService;
    private final FindingService findingService;
    private final RiskService riskService;
    private final TrustAssessmentService trustAssessmentService;

    public WebsiteController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService,
            EvidenceService evidenceService,
            FindingService findingService,
            RiskService riskService,
            TrustAssessmentService trustAssessmentService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
        this.evidenceService = evidenceService;
        this.findingService = findingService;
        this.riskService = riskService;
        this.trustAssessmentService = trustAssessmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("websites", websiteService.findAll());
        return "websites/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("request", new WebsiteCreateRequest());
        return "websites/new";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("request") WebsiteCreateRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "websites/new";
        }

        try {
            Website website = websiteService.create(request);
            return "redirect:/websites/" + website.getId();
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "websites/new";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("website", websiteService.findById(id));
        model.addAttribute("monitoringRuns", monitoringRunService.findByWebsiteId(id));

        model.addAttribute("monitoringRunCount", monitoringRunService.countByWebsiteId(id));
        model.addAttribute("collectedEvidenceCount", evidenceService.countCollectedEvidenceByWebsiteId(id));
        model.addAttribute("normalizedEvidenceCount", evidenceService.countNormalizedEvidenceByWebsiteId(id));
        model.addAttribute("findingCount", findingService.countByWebsiteId(id));
        model.addAttribute("riskCount", riskService.countByWebsiteId(id));
        model.addAttribute("trustAssessmentCount", trustAssessmentService.countByWebsiteId(id));

        return "websites/detail";
    }
}