package com.filmpin.repository;

import com.filmpin.entity.FilmRoll;
import com.filmpin.entity.FilmRollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRollRepository extends JpaRepository<FilmRoll, Long> {
    List<FilmRoll> findByCameraIdOrderByCreatedAtDesc(Long cameraId);
    
    @Query("SELECT r FROM FilmRoll r WHERE r.camera.user.username = :username AND r.status = :status ORDER BY r.createdAt DESC")
    List<FilmRoll> findByUsernameAndStatus(@Param("username") String username, @Param("status") FilmRollStatus status);
}
