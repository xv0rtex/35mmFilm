package com.filmpin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"roll_id", "photo_number"})
})
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roll_id", nullable = false)
    private FilmRoll filmRoll;

    @Column(name = "photo_number", nullable = false)
    private Integer photoNumber;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "aperture")
    private String aperture;

    @Column(name = "exposure")
    private String exposure;

    @Column(name = "white_balance")
    private String whiteBalance;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Photo() {
    }

    public Photo(Long id, FilmRoll filmRoll, Integer photoNumber, LocalDateTime timestamp, Double latitude, Double longitude, String aperture, String exposure, String whiteBalance, LocalDateTime createdAt) {
        this.id = id;
        this.filmRoll = filmRoll;
        this.photoNumber = photoNumber;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.aperture = aperture;
        this.exposure = exposure;
        this.whiteBalance = whiteBalance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FilmRoll getFilmRoll() {
        return filmRoll;
    }

    public void setFilmRoll(FilmRoll filmRoll) {
        this.filmRoll = filmRoll;
    }

    public Integer getPhotoNumber() {
        return photoNumber;
    }

    public void setPhotoNumber(Integer photoNumber) {
        this.photoNumber = photoNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        if (imageUrl != null && imageUrl.startsWith("/images/uploads/")) {
            return "/images/uploads/thumb/" + imageUrl.substring("/images/uploads/".length());
        }
        return imageUrl;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getExposure() {
        return exposure;
    }

    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
