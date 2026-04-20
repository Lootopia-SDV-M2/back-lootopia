package com.supdevinci.lootopia.repository;

import com.supdevinci.lootopia.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByWinnerIdOrderByCreatedAtDesc(Long winnerId);

    Optional<Voucher> findByCode(String code);
}
