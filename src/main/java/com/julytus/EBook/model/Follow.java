package com.julytus.EBook.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.List;

public class Follow extends BaseEntity<String> {
    @OneToOne
    private User user;

    @OneToMany(mappedBy = "follow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowDetail> followDetailList;
}
