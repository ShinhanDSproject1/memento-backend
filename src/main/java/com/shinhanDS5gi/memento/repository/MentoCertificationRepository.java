package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.MentoCertification;
import org.springframework.data.jpa.repository.JpaRepository;
//멘토 자격증 저장용
public interface MentoCertificationRepository extends JpaRepository<MentoCertification, Long> {}