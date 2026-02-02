package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "social")
public class Social extends BaseEntity {

    @Column(length = 120)
    private String facebook;

    @Column(length = 120)
    private String line;

    @Column(length = 120)
    private String instagram;

    @Column(length = 120)
    private String tiktok;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false) //Fk
    private User user;
}
