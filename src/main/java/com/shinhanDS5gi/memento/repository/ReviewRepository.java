package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // memberSeq와 mentosSeq로 이미 작성된 리뷰가 있는지 확인하는 메서드
    boolean existsByMember_MemberSeqAndMentos_MentosSeq(Long memberSeq, Long mentosSeq);
}
