package com.supdevinci.lootopia.repository;

import com.supdevinci.lootopia.model.Hunt;
import com.supdevinci.lootopia.model.enums.HuntStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HuntRepository extends JpaRepository<Hunt, Long> {
    List<Hunt> findByCreatorId(Long creatorId);
    List<Hunt> findByStatus(HuntStatus status);
}
