package com.wteam.backend.item;

import com.wteam.backend.common.interfaces.Mapper;
import com.wteam.backend.item.dto.ItemRequest;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.item_image.ItemImageMapper;
import com.wteam.backend.user_profile.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper implements Mapper<ItemRequest, ItemResponse, Item> {
    private final UserProfileMapper userProfileMapper;
    private final ItemImageMapper itemImageMapper;

    @Override
    public ItemResponse toResponse(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }

        List<String> tags = item.getTags() != null
                ? Arrays.asList(item.getTags())
                : null;

        return new ItemResponse(
                item.getId(),
                item.getOwner().getId(),
                item.getOwner().getUserProfile() != null
                        ? userProfileMapper.toPublicProfileResponse(item.getOwner().getUserProfile())
                        : null,
                item.getCategory().getId(),
                item.getTitle(),
                item.getDescription(),
                tags,
                item.getCondition(),
                item.getPricePerDay(),
                item.getPricePerWeek(),
                item.getDepositAmount(),
                item.getStatus(),
                item.getCity(),
                item.getAddress(),
                item.getLatitude(),
                item.getLongitude(),
                item.isVerified(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getImages() != null ? item.getImages().stream().map(itemImageMapper::toResponse).collect(Collectors.toList()) : null
        );
    }

    public void updateItemFromRequest(Item item, ItemRequest request) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }

        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setTags(request.tags() != null ? request.tags().toArray(new String[0]) : null);
        item.setCondition(request.condition());
        item.setPricePerDay(request.pricePerDay());
        item.setPricePerWeek(request.pricePerWeek());
        item.setDepositAmount(request.depositAmount());
        item.setCity(request.city());
        item.setAddress(request.address());
        item.setLatitude(request.latitude());
        item.setLongitude(request.longitude());
    }

    @Override
    public Item toEntity(ItemRequest dto) {
        return null;
    }
}
