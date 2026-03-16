package com.outfy.outfy_backend.infrastructure.scheduler;

import com.outfy.outfy_backend.modules.draft.entity.Draft;
import com.outfy.outfy_backend.modules.draft.enums.DraftStatus;
import com.outfy.outfy_backend.modules.draft.repository.DraftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DraftCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DraftCleanupScheduler.class);
    private static final int DRAFT_CLEANUP_DAYS = 15;

    private final DraftRepository draftRepository;

    public DraftCleanupScheduler(DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    /**
     * Run daily at 2 AM to cleanup old draft items
     * Deletes drafts with status DRAFT or ANALYZED older than 15 days
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldDrafts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(DRAFT_CLEANUP_DAYS);
        
        List<Draft> oldDrafts = draftRepository.findByStatusAndCreatedAtBefore(
                DraftStatus.ANALYZED, 
                cutoffDate
        );

        if (!oldDrafts.isEmpty()) {
            draftRepository.deleteAll(oldDrafts);
            logger.info("Cleaned up {} old draft clothing items older than {} days", 
                    oldDrafts.size(), DRAFT_CLEANUP_DAYS);
        } else {
            logger.info("No old drafts to clean up");
        }
    }
}

