package com.filmpin.repository;

import com.filmpin.entity.Photo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByFilmRollId(Long rollId);
    Optional<Photo> findByImageUrl(String imageUrl);
    
    @Query("SELECT COALESCE(MAX(p.photoNumber), 0) FROM Photo p WHERE p.filmRoll.id = :rollId")
    Integer findMaxPhotoNumberByRollId(Long rollId);

    @Query("SELECT COUNT(p) FROM Photo p WHERE p.filmRoll.camera.user.username = :username")
    long countPhotosByUsername(String username);

    @Query("SELECT p FROM Photo p WHERE p.filmRoll.camera.user.username = :username")
    List<Photo> findAllByUsername(String username, Sort sort);
}
