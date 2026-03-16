package com.outfy.outfy_backend.modules.wardrobe.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import com.outfy.outfy_backend.modules.clothing.repository.ClothingItemRepository;
import com.outfy.outfy_backend.modules.wardrobe.dto.request.CreateWardrobeItemRequest;
import com.outfy.outfy_backend.modules.wardrobe.dto.response.WardrobeItemResponse;
import com.outfy.outfy_backend.modules.wardrobe.entity.WardrobeItem;
import com.outfy.outfy_backend.modules.wardrobe.mapper.WardrobeMapper;
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
    private final WardrobeMapper wardrobeMapper;

    public WardrobeService(
            WardrobeItemRepository wardrobeItemRepository,
            ClothingItemRepository clothingItemRepository,
            WardrobeMapper wardrobeMapper) {
        this.wardrobeItemRepository = wardrobeItemRepository;
        this.clothingItemRepository = clothingItemRepository;
        this.wardrobeMapper = wardrobeMapper;
    }

    @Transactional
    public WardrobeItemResponse createWardrobeItem(CreateWardrobeItemRequest request) {
        logger.info("Creating wardrobe item for user: {}", request.getUserId());

        // Use mapper to convert request to entity
        WardrobeItem item = wardrobeMapper.toEntity(request);
        item.setIsFavorite(false);

        WardrobeItem saved = wardrobeItemRepository.save(item);
        logger.info("Created wardrobe item with id: {}", saved.getId());

        return toResponseWithImageUrl(saved);
    }

    public WardrobeItemResponse getWardrobeItemById(Long id) {
        WardrobeItem item = wardrobeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WardrobeItem", "id", id));
        return toResponseWithImageUrl(item);
    }

    public List<WardrobeItemResponse> getWardrobeItemsByUserId(Long userId) {
        return wardrobeItemRepository.findByUserId(userId).stream()
                .map(this::toResponseWithImageUrl)
                .collect(Collectors.toList());
    }

    public List<WardrobeItemResponse> getWardrobeItemsByCategory(Long userId, String category) {
        return wardrobeItemRepository.findByUserIdAndCategory(userId, category).stream()
                .map(this::toResponseWithImageUrl)
                .collect(Collectors.toList());
    }

    public List<WardrobeItemResponse> getFavoriteItems(Long userId) {
        return wardrobeItemRepository.findByUserIdAndIsFavorite(userId, true).stream()
                .map(this::toResponseWithImageUrl)
                .collect(Collectors.toList());
    }

    public List<WardrobeItemResponse> getWardrobeItemsBySeason(Long userId, String season) {
        return wardrobeItemRepository.findByUserIdAndSeason(userId, season).stream()
                .map(this::toResponseWithImageUrl)
                .collect(Collectors.toList());
    }

    @Transactional
    public WardrobeItemResponse toggleFavorite(Long id) {
        WardrobeItem item = wardrobeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WardrobeItem", "id", id));

        item.setIsFavorite(!item.getIsFavorite());
        WardrobeItem saved = wardrobeItemRepository.save(item);

        logger.info("Toggled favorite for wardrobe item id: {} to {}", id, saved.getIsFavorite());

        return toResponseWithImageUrl(saved);
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

        return toResponseWithImageUrl(saved);
    }

    @Transactional
    public void deleteWardrobeItem(Long id) {
        if (!wardrobeItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("WardrobeItem", "id", id);
        }
        wardrobeItemRepository.deleteById(id);
        logger.info("Deleted wardrobe item with id: {}", id);
    }

    /**
     * Add analyzed clothing item to wardrobe
     */
    @Transactional
    public WardrobeItemResponse addToWardrobe(Long clothingItemId, Long userId, String season, String notes) {
        logger.info("Adding clothing item {} to wardrobe for user {}", clothingItemId, userId);

        // Verify clothing item exists
        ClothingItem clothingItem = clothingItemRepository.findById(clothingItemId)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", clothingItemId));

        // Verify ownership
        if (!clothingItem.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Clothing item does not belong to user");
        }

        // Check if already in wardrobe (has clothingItemId)
        boolean alreadyExists = wardrobeItemRepository.findByUserId(userId).stream()
                .anyMatch(item -> clothingItemId.equals(item.getClothingItemId()));

        if (alreadyExists) {
            throw new IllegalStateException("Clothing item already in wardrobe");
        }

        // Create wardrobe item
        WardrobeItem wardrobeItem = new WardrobeItem();
        wardrobeItem.setUserId(userId);
        wardrobeItem.setClothingItemId(clothingItemId);
        wardrobeItem.setCategory(clothingItem.getGarmentCategory());
        wardrobeItem.setColor(clothingItem.getColor());
        wardrobeItem.setSeason(season);
        wardrobeItem.setNotes(notes);
        wardrobeItem.setIsFavorite(false);

        WardrobeItem saved = wardrobeItemRepository.save(wardrobeItem);
        logger.info("Added clothing item {} to wardrobe with id {}", clothingItemId, saved.getId());

        return toResponseWithImageUrl(saved);
    }

    private WardrobeItemResponse toResponseWithImageUrl(WardrobeItem item) {
        // Use mapper for basic mapping
        WardrobeItemResponse response = wardrobeMapper.toResponse(item);

        // Get image URL from clothing item if available
        if (item.getClothingItemId() != null) {
            clothingItemRepository.findById(item.getClothingItemId())
                    .ifPresent(clothing -> response.setImageUrl(clothing.getImageUrl()));
        }

        return response;
    }
}

