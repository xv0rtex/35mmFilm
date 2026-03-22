package com.filmpin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cameras")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "purchase_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CameraType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Camera() {
    }

    public Camera(Long id, User user, String name, LocalDate purchaseDate, CameraType type, String description, String imagePath, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.type = type;
        this.description = description;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public CameraType getType() {
        return type;
    }

    public void setType(CameraType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
