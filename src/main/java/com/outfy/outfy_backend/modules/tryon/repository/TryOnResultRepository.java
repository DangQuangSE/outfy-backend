package com.outfy.outfy_backend.modules.tryon.repository;

import com.outfy.outfy_backend.modules.tryon.entity.TryOnResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TryOnResultRepository extends JpaRepository<TryOnResultEntity, Long> {
    Optional<TryOnResultEntity> findByTryonSessionId(Long tryonSessionId);
}

