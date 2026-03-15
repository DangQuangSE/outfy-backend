package com.outfy.outfy_backend.modules.clothing.repository;

import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findByUserId(Long userId);
}

