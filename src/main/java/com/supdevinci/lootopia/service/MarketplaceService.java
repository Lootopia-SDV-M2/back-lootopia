package com.supdevinci.lootopia.service;

import com.supdevinci.lootopia.controller.dto.BuyResponse;
import com.supdevinci.lootopia.controller.dto.ListArtefactRequest;
import com.supdevinci.lootopia.controller.dto.MarketListingResponse;
import com.supdevinci.lootopia.model.Artefact;
import com.supdevinci.lootopia.model.MarketListing;
import com.supdevinci.lootopia.model.Transaction;
import com.supdevinci.lootopia.model.User;
import com.supdevinci.lootopia.model.enums.ListingStatus;
import com.supdevinci.lootopia.model.enums.ListingType;
import com.supdevinci.lootopia.model.enums.TransactionType;
import com.supdevinci.lootopia.repository.ArtefactRepository;
import com.supdevinci.lootopia.repository.MarketListingRepository;
import com.supdevinci.lootopia.repository.TransactionRepository;
import com.supdevinci.lootopia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MarketplaceService {
    private final MarketListingRepository marketListingRepository;
    private final ArtefactRepository artefactRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<MarketListingResponse> getActiveListings() {
        return marketListingRepository.findByStatusOrderByCreatedAtDesc(ListingStatus.ACTIF)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MarketListingResponse> getMyListings(User seller) {
        return marketListingRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MarketListingResponse listArtefact(ListArtefactRequest req, User seller) {
        Artefact artefact = artefactRepository.findById(req.getArtefactId())
            .orElseThrow(() -> new RuntimeException("Artefact introuvable"));
        if (!artefact.getOwner().getId().equals(seller.getId())) {
            throw new RuntimeException("Cet artefact ne vous appartient pas");
        }
        MarketListing listing = new MarketListing();
        listing.setArtefact(artefact);
        listing.setSeller(seller);
        listing.setPrice(req.getPrice());
        listing.setType(ListingType.valueOf(req.getType()));
        listing.setStatus(ListingStatus.ACTIF);
        listing.setCreatedAt(LocalDateTime.now());
        return toResponse(marketListingRepository.save(listing));
    }

    public BuyResponse buy(Long listingId, User buyer) {
        MarketListing listing = marketListingRepository.findById(listingId)
            .orElseThrow(() -> new RuntimeException("Annonce introuvable"));
        if (listing.getStatus() != ListingStatus.ACTIF) {
            throw new RuntimeException("Cette annonce n'est plus disponible");
        }
        if (listing.getSeller().getId().equals(buyer.getId())) {
            throw new RuntimeException("Vous ne pouvez pas acheter votre propre artefact");
        }
        if (buyer.getWalletBalance().compareTo(listing.getPrice()) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }

        User seller = listing.getSeller();
        buyer.setWalletBalance(buyer.getWalletBalance().subtract(listing.getPrice()));
        seller.setWalletBalance(seller.getWalletBalance().add(listing.getPrice()));
        listing.getArtefact().setOwner(buyer);
        listing.setStatus(ListingStatus.VENDU);

        userRepository.save(buyer);
        userRepository.save(seller);
        artefactRepository.save(listing.getArtefact());
        marketListingRepository.save(listing);

        Transaction tx = new Transaction();
        tx.setSender(buyer);
        tx.setReceiver(seller);
        tx.setAmount(listing.getPrice());
        tx.setType(TransactionType.ACHAT);
        tx.setCreatedAt(LocalDateTime.now());
        Transaction saved = transactionRepository.save(tx);

        return new BuyResponse("Achat realise avec succes", saved.getId());
    }

    private MarketListingResponse toResponse(MarketListing l) {
        MarketListingResponse r = new MarketListingResponse();
        r.setId(l.getId());
        r.setSellerName(l.getSeller().getNom() + " " + l.getSeller().getPrenom());
        r.setPrice(l.getPrice());
        r.setType(l.getType().name());
        r.setStatus(l.getStatus().name());
        r.setCreatedAt(l.getCreatedAt().toString());
        MarketListingResponse.ArtefactInfo ai = new MarketListingResponse.ArtefactInfo();
        ai.setId(l.getArtefact().getId());
        ai.setName(l.getArtefact().getName());
        ai.setImageUrl(l.getArtefact().getImageUrl());
        ai.setRarity(l.getArtefact().getRarity());
        r.setArtefact(ai);
        return r;
    }
}
