package com.julytus.EBook.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

public class FollowDetail extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    private Follow follow;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
}
