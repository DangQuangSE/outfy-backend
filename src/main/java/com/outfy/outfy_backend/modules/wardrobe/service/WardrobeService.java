package com.outfy.outfy_backend.modules.wardrobe.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import com.outfy.outfy_backend.modules.clothing.repository.ClothingItemRepository;
import com.outfy.outfy_backend.modules.wardrobe.dto.request.CreateWardrobeItemRequest;
import com.outfy.outfy_backend.modules.wardrobe.dto.response.WardrobeItemResponse;
import com.outfy.outfy_backend.modules.wardrobe.entity.WardrobeItem;
import com.outfy.outfy_backend.modules.wardrobe.repository.WardrobeItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WardrobeService {

    private static final Logger logger = LoggerFactory.getLogger(WardrobeService.class);

    private final WardrobeItemRepository wardrobeItemRepository;
    private final ClothingItemRepository clothingItemRepository;

    public WardrobeService(
            WardrobeItemRepository wardrobeItemRepository,
            ClothingItemRepository clothingItemRepository) {
        this.wardrobeItemRepository = wardrobeItemRepository;
        this.clothingItemRepository = clothingItemRepository;
    }

    @Transactional
    public WardrobeItemResponse createWardrobeItem(CreateWardrobeItemRequest request) {
        logger.info("Creating wardrobe item for user: {}", request.getUserId());

        WardrobeItem item = new WardrobeItem();
        item.setUserId(request.getUserId());
        item.setClothingItemId(request.getClothingItemId());
        item.setCategory(request.getCategory());
        item.setSeason(request.getSeason());
        item.setColor(request.getColor());
        item.setNotes(request.getNotes());
        item.setIsFavorite(false);

        WardrobeItem saved = wardrobeItemRepository.save(item);
        logger.info("Created wardrobe item with id: {}", saved.getId());

        return toResponse(saved);
    }

    public WardrobeItemResponse getWardrobeItemById(Long id) {
        WardrobeItem item = wardrobeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WardrobeItem", "id", id));
        return toResponse(item);
    }

    public List<WardrobeItemResponse> getWardrobeItemsByUserId(Long userId) {
        return wardrobeItemRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WardrobeItemResponse> getWardrobeItemsByCategory(Long userId, String category) {
        return wardrobeItemRepository.findByUserIdAndCategory(userId, category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WardrobeItemResponse> getFavoriteItems(Long userId) {
        return wardrobeItemRepository.findByUserIdAndIsFavorite(userId, true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WardrobeItemResponse> getWardrobeItemsBySeason(Long userId, String season) {
        return wardrobeItemRepository.findByUserIdAndSeason(userId, season).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WardrobeItemResponse toggleFavorite(Long id) {
        WardrobeItem item = wardrobeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WardrobeItem", "id", id));

        item.setIsFavorite(!item.getIsFavorite());
        WardrobeItem saved = wardrobeItemRepository.save(item);

        logger.info("Toggled favorite for wardrobe item id: {} to {}", id, saved.getIsFavorite());

        return toResponse(saved);
    }

    @Transactional
    public WardrobeItemResponse updateWardrobeItem(Long id, CreateWardrobeItemRequest request) {
        WardrobeItem item = wardrobeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WardrobeItem", "id", id));

        item.setCategory(request.getCategory());
        item.setSeason(request.getSeason());
        item.setColor(request.getColor());
        item.setNotes(request.getNotes());

        WardrobeItem saved = wardrobeItemRepository.save(item);
        logger.info("Updated wardrobe item with id: {}", id);

        return toResponse(saved);
    }

    @Transactional
    public void deleteWardrobeItem(Long id) {
        if (!wardrobeItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("WardrobeItem", "id", id);
        }
        wardrobeItemRepository.deleteById(id);
        logger.info("Deleted wardrobe item with id: {}", id);
    }

    private WardrobeItemResponse toResponse(WardrobeItem item) {
        WardrobeItemResponse response = new WardrobeItemResponse();
        response.setId(item.getId());
        response.setUserId(item.getUserId());
        response.setClothingItemId(item.getClothingItemId());
        response.setCategory(item.getCategory());
        response.setSeason(item.getSeason());
        response.setColor(item.getColor());
        response.setIsFavorite(item.getIsFavorite());
        response.setNotes(item.getNotes());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        // Get image URL from clothing item if available
        if (item.getClothingItemId() != null) {
            clothingItemRepository.findById(item.getClothingItemId())
                    .ifPresent(clothing -> response.setImageUrl(clothing.getImageUrl()));
        }

        return response;
    }
}

