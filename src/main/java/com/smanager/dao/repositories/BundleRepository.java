package com.smanager.dao.repositories;

import com.smanager.dao.models.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    @Query(value = "select distinct * from bundles " +
            "where bundle = :bundle " +
            "and key_value = :keyValue " +
            "and lang = :lang",
    nativeQuery = true)
    Bundle findBundle(@Param("lang") String lang, @Param("bundle") String bundle,
                       @Param("keyValue") String keyValue);
}
