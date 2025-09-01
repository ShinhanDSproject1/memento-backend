package com.shinhanDS5gi.memento.repository;

import com.shinhanDS5gi.memento.domain.Category;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategorySeqAndStatus(Long categorySeq, BaseStatus status);
}
