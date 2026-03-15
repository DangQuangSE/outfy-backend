package com.outfy.outfy_backend.modules.tryon.repository;

import com.outfy.outfy_backend.modules.tryon.entity.TryOnSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TryOnSessionRepository extends JpaRepository<TryOnSession, Long> {
    List<TryOnSession> findByUserId(Long userId);
}

