package com.cigabyte.sitesentinel.website;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "websites",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_websites_domain", columnNames = "domain")
        }
)
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, length = 255)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WebsiteStatus status = WebsiteStatus.ACTIVE;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    protected Website() {
    }

    public Website(String name, String domain) {
        this.name = name;
        this.domain = domain;
        this.status = WebsiteStatus.ACTIVE;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = WebsiteStatus.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public WebsiteStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void activate() {
        this.status = WebsiteStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = WebsiteStatus.INACTIVE;
    }
}