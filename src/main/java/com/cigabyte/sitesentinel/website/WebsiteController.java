package com.cigabyte.sitesentinel.website;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cigabyte.sitesentinel.monitoring.MonitoringRunService;

import java.util.UUID;

@Controller
@RequestMapping("/websites")
public class WebsiteController {

    private final WebsiteService websiteService;
    private final MonitoringRunService monitoringRunService;

    public WebsiteController(
            WebsiteService websiteService,
            MonitoringRunService monitoringRunService
    ) {
        this.websiteService = websiteService;
        this.monitoringRunService = monitoringRunService;
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
        return "websites/detail";
    }
}