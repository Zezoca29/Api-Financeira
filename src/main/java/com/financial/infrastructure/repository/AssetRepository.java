package com.financial.infrastructure.repository;

import com.financial.domain.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByTicker(String ticker);
    List<Asset> findByActiveTrue();
    
    @Query("SELECT a FROM Asset a WHERE a.category = :category AND a.active = true")
    List<Asset> findByCategoryAndActive(String category);
    
    @Query("SELECT DISTINCT a.category FROM Asset a WHERE a.active = true")
    List<String> findAllCategories();
}
