package com.cigabyte.sitesentinel.dashboard;

import com.cigabyte.sitesentinel.website.WebsiteRepository;
import com.cigabyte.sitesentinel.website.WebsiteStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final WebsiteRepository websiteRepository;

    public DashboardController(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        long totalWebsites = websiteRepository.count();
        long activeWebsites = websiteRepository.findAll()
                .stream()
                .filter(website -> website.getStatus() == WebsiteStatus.ACTIVE)
                .count();

        model.addAttribute("totalWebsites", totalWebsites);
        model.addAttribute("activeWebsites", activeWebsites);

        return "dashboard/index";
    }
}