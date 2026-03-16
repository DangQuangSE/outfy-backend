package com.outfy.outfy_backend.modules.draft.repository;

import com.outfy.outfy_backend.modules.draft.entity.Draft;
import com.outfy.outfy_backend.modules.draft.enums.DraftStatus;
import com.outfy.outfy_backend.modules.draft.enums.DraftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {

    List<Draft> findByUserId(Long userId);

    List<Draft> findByUserIdAndDraftType(Long userId, DraftType draftType);

    List<Draft> findByUserIdAndStatus(Long userId, DraftStatus status);

    List<Draft> findByUserIdAndDraftTypeAndStatus(Long userId, DraftType draftType, DraftStatus status);

    List<Draft> findByStatusAndCreatedAtBefore(DraftStatus status, LocalDateTime dateTime);

    void deleteByIdAndUserId(Long id, Long userId);
}

