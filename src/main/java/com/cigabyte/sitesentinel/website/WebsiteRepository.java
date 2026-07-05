package com.cigabyte.sitesentinel.website;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WebsiteRepository extends JpaRepository<Website, UUID> {

    boolean existsByDomain(String domain);

    Optional<Website> findByDomain(String domain);
}