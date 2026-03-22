package com.filmpin.repository;

import com.filmpin.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByUserId(Long userId);
    Optional<Camera> findByImagePath(String imagePath);
}
