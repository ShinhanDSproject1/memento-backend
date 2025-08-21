package com.shinhanDS5gi.memento.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhanDS5gi.memento.domain.base.BaseStatus;
import com.shinhanDS5gi.memento.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long categorySeq;

    @Column(nullable = false)
    private String categoryName;
    @Column(nullable = false)
    private String categoryDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Mentos> mentosList = new ArrayList<>();

}
