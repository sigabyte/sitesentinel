package com.cigabyte.sitesentinel.website;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WebsiteCreateRequest {

    @NotBlank(message = "Website name is required.")
    @Size(max = 180, message = "Website name must not exceed 180 characters.")
    private String name;

    @NotBlank(message = "Domain is required.")
    @Size(max = 255, message = "Domain must not exceed 255 characters.")
    private String domain;

    public WebsiteCreateRequest() {
    }

    public WebsiteCreateRequest(String name, String domain) {
        this.name = name;
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}