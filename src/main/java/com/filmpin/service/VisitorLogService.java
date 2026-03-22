package com.filmpin.service;

import com.filmpin.entity.VisitorLog;
import com.filmpin.repository.VisitorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitorLogService {

    @Autowired
    private VisitorLogRepository visitorLogRepository;

    @Transactional
    public void saveLog(VisitorLog log) {
        visitorLogRepository.save(log);
    }
}
