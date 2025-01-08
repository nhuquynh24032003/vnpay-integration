package com.white.apidoc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Table(name = "transactions") // Đổi tên bảng thành "users"
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // Giả sử bạn quản lý người dùng
    private Long amount;
    private String bankCode;
    private String status;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
