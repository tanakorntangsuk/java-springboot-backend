package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "user_product")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 60)
    private String email;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(nullable = false, length = 120)
    private String name;

    private String civilId;

    @OneToOne(mappedBy = "user",
            orphanRemoval = true) //อนุญาติให้ลบ ตัวแม่ที่ fk เชื่อม
    private Social social;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Address> addresses;

    private boolean activated; // เช็คว่ายืนยันเมล การสมัครรึยัง

    private String token; // ไม่ใช่ JWT token การส่งเมล

    private Date tokenExpire;
}
