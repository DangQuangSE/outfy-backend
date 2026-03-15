package com.outfy.outfy_backend.modules.bodyprofile.repository;

import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyGenerationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BodyGenerationResultRepository extends JpaRepository<BodyGenerationResultEntity, Long> {
    Optional<BodyGenerationResultEntity> findByBodyProfileId(Long bodyProfileId);
}

