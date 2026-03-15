package com.outfy.outfy_backend.modules.wardrobe.repository;

import com.outfy.outfy_backend.modules.wardrobe.entity.WardrobeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardrobeItemRepository extends JpaRepository<WardrobeItem, Long> {
    List<WardrobeItem> findByUserId(Long userId);
    
    List<WardrobeItem> findByUserIdAndCategory(Long userId, String category);
    
    List<WardrobeItem> findByUserIdAndIsFavorite(Long userId, Boolean isFavorite);
    
    @Query("SELECT w FROM WardrobeItem w WHERE w.userId = :userId AND w.season = :season")
    List<WardrobeItem> findByUserIdAndSeason(@Param("userId") Long userId, @Param("season") String season);
}

