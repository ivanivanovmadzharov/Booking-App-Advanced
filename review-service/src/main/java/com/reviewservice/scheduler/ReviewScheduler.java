package com.reviewservice.scheduler;

import com.reviewservice.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReviewScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReviewScheduler.class);

    private final ReviewService reviewService;

    public ReviewScheduler(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Cron job: runs every day at midnight.
     * Flags low-rated reviews (rating < 3) older than 30 days for removal.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void flagOldLowRatedReviews() {
        log.info("Running scheduled job: flagging old low-rated reviews");
        reviewService.flagOldLowRatedReviews();
    }

    /**
     * Fixed-rate job: runs every 6 hours.
     * Deletes all previously flagged reviews to keep the dataset clean.
     */
    @Scheduled(fixedRate = 21_600_000)
    public void deleteFlaggedReviews() {
        log.info("Running scheduled job: deleting flagged reviews");
        reviewService.deleteFlaggedReviews();
    }
}
