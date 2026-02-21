package com.juyel.blackrock.challenge.validation;

import com.juyel.blackrock.challenge.api.dto.TransactionResponse;
import com.juyel.blackrock.challenge.validator.DuplicateTransactionDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class DuplicateTransactionDetectorTest {

    private  DuplicateTransactionDetector detector;

    @BeforeEach
    void setUp() {
        detector = new DuplicateTransactionDetector();
    }

    @Test
    void shouldReturnFalseForUniqueTransactionDate() {

        Set<LocalDateTime> tracker = detector.newTracker();

        LocalDateTime now = LocalDateTime.now();

        TransactionResponse tx = new TransactionResponse(
                now,
                500,
                600,
                100
        );

        boolean isDuplicate = detector.isDuplicate(tx, tracker);

        assertThat(isDuplicate).isFalse();
        assertThat(tracker).containsExactly(now);
    }

    @Test
    void shouldReturnTrueForDuplicateTransactionDate() {

        Set<LocalDateTime> tracker = detector.newTracker();

        LocalDateTime now = LocalDateTime.now();

        TransactionResponse tx1 = new TransactionResponse(now, 500, 600, 100);
        TransactionResponse tx2 = new TransactionResponse(now, 700, 800, 100);

        detector.isDuplicate(tx1, tracker); // first insert

        boolean isDuplicate = detector.isDuplicate(tx2, tracker);

        assertThat(isDuplicate).isTrue();
        assertThat(tracker).hasSize(1);
    }

    @Test
    void shouldInitializeEmptyTracker() {

        Set<LocalDateTime> tracker = detector.newTracker();

        assertThat(tracker).isNotNull();
        assertThat(tracker).isEmpty();
    }

    @Test
    void shouldFailIfTrackerIsNull() {
        TransactionResponse tx = new TransactionResponse(LocalDateTime.now(), 100, 200, 100);

        assertThatThrownBy(() -> detector.isDuplicate(tx, null))
                .isInstanceOf(NullPointerException.class);
    }
}
