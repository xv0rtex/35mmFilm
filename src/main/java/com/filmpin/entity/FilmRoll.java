package com.filmpin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "film_rolls")
public class FilmRoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "purchase_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @Column(name = "expiry_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Integer iso;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "film_price")
    private Double filmPrice;

    @Column(name = "development_price")
    private Double developmentPrice;

    @Column(name = "push_pull")
    private Integer pushPull = 0;

    @Column(name = "load_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate loadDate;

    @Column(name = "film_type")
    private String filmType = "Color"; // "Color" or "B&W"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilmRollStatus status = FilmRollStatus.UNEXPOSED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public FilmRoll() {
    }

    public FilmRoll(Long id, Camera camera, String name, LocalDate purchaseDate, LocalDate expiryDate, Integer iso, String brand, String description, Double filmPrice, Double developmentPrice, FilmRollStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.camera = camera;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.iso = iso;
        this.brand = brand;
        this.description = description;
        this.filmPrice = filmPrice;
        this.developmentPrice = developmentPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getIso() {
        return iso;
    }

    public void setIso(Integer iso) {
        this.iso = iso;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getFilmPrice() {
        return filmPrice;
    }

    public void setFilmPrice(Double filmPrice) {
        this.filmPrice = filmPrice;
    }

    public Double getDevelopmentPrice() {
        return developmentPrice;
    }

    public void setDevelopmentPrice(Double developmentPrice) {
        this.developmentPrice = developmentPrice;
    }

    public Integer getPushPull() {
        return pushPull;
    }

    public void setPushPull(Integer pushPull) {
        this.pushPull = pushPull;
    }

    public LocalDate getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(LocalDate loadDate) {
        this.loadDate = loadDate;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }

    public FilmRollStatus getStatus() {
        return status;
    }

    public void setStatus(FilmRollStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
