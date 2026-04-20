package com.supdevinci.lootopia.repository;

import com.supdevinci.lootopia.model.MarketListing;
import com.supdevinci.lootopia.model.enums.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketListingRepository extends JpaRepository<MarketListing, Long> {
    List<MarketListing> findByStatusOrderByCreatedAtDesc(ListingStatus status);
    List<MarketListing> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
}
