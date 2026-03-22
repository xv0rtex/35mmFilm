package com.filmpin.repository;

import com.filmpin.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
}
