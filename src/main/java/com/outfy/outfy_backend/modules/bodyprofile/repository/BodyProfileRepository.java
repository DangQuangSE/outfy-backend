package com.outfy.outfy_backend.modules.bodyprofile.repository;

import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyProfileRepository extends JpaRepository<BodyProfile, Long> {
    List<BodyProfile> findByUserId(Long userId);
}

