package com.wteam.backend.item;

import com.wteam.backend.item.dto.ItemRequest;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/available")
    public ResponseEntity<Page<ItemResponse>> getAllItemsWhichAreAvailable(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItemsWhichAreAvailable(pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ItemResponse>> getMyItems(
            @CurrentUser UserPrincipalDto user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(itemService.getMyItems(user.id(), pageable));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ItemResponse>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemResponse> createItem(
            @CurrentUser UserPrincipalDto user,
            @Valid @RequestBody ItemRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(user.id(), request));
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @CurrentUser UserPrincipalDto user,
            @Valid @RequestBody ItemRequest request
    ) {
        return ResponseEntity.ok(itemService.updateItem(itemId, user.id(), request));
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            @CurrentUser UserPrincipalDto user
    ) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        itemService.deleteItem(itemId, user.id(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{itemId}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> setItemVerification(
            @PathVariable Long itemId,
            @RequestParam boolean verified
    ) {
        return ResponseEntity.ok(itemService.setItemVerified(itemId, verified));
    }
}
