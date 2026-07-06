package com.cigabyte.sitesentinel.website;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class WebsiteService {

    private final WebsiteRepository websiteRepository;
    private final WebsiteTargetValidator websiteTargetValidator;


    public WebsiteService(
            WebsiteRepository websiteRepository,
            WebsiteTargetValidator websiteTargetValidator
    ) {
        this.websiteRepository = websiteRepository;
        this.websiteTargetValidator = websiteTargetValidator;
    }

    @Transactional(readOnly = true)
    public List<Website> findAll() {
        return websiteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Website findById(UUID id) {
        return websiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Website not found: " + id));
    }

    @Transactional
    public Website create(WebsiteCreateRequest request) {
        String normalizedDomain = normalizeDomain(request.getDomain());

        websiteTargetValidator.validateConfiguredHost(normalizedDomain);

        if (websiteRepository.existsByDomain(normalizedDomain)) {
            throw new IllegalArgumentException("This domain is already monitored.");
        }

        Website website = new Website(request.getName().trim(), normalizedDomain);
        return websiteRepository.save(website);
    }

    String normalizeDomain(String rawDomain) {
        if (rawDomain == null || rawDomain.isBlank()) {
            throw new IllegalArgumentException("Domain is required.");
        }

        String value = rawDomain.trim().toLowerCase(Locale.ROOT);

        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }

        URI uri = URI.create(value);

        String host = uri.getHost();

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Invalid domain.");
        }

        if (host.startsWith("www.")) {
            host = host.substring(4);
        }

        return host;
    }
}