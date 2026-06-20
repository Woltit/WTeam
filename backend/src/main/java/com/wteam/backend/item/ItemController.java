package com.wteam.backend.item;

import com.wteam.backend.item.dto.ItemRequest;
import com.wteam.backend.item.dto.ItemResponse;
import com.wteam.backend.item_image.ItemImageService;
import com.wteam.backend.item_image.dto.ItemImageResponse;
import com.wteam.backend.security.annotation.CurrentUser;
import com.wteam.backend.security.dto.UserPrincipalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Товари", description = "API для управління речами, які здаються в оренду, та їхніми фотографіями")
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemImageService itemImageService;

    @Operation(summary = "Отримати доступні товари", description = "Повертає список товарів, які наразі доступні для оренди (видимі для всіх).")
    @GetMapping("/available")
    public ResponseEntity<Page<ItemResponse>> getAllItemsWhichAreAvailable(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItemsWhichAreAvailable(pageable));
    }

    @Operation(summary = "Мої товари", description = "Повертає список товарів, які створив поточний користувач.")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ItemResponse>> getMyItems(
            @CurrentUser UserPrincipalDto user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(itemService.getMyItems(user.id(), pageable));
    }

    @Operation(summary = "Отримати товар за ID", description = "Повертає детальну інформацію про конкретний товар.")
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @Operation(summary = "Всі товари (Адмін)", description = "Тільки для адміністраторів. Повертає список абсолютно всіх товарів у системі.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ItemResponse>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @Operation(summary = "Створити товар", description = "Додає нову річ для оренди.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemResponse> createItem(
            @CurrentUser UserPrincipalDto user,
            @Valid @RequestBody ItemRequest request
    ) {
        ItemResponse item = itemService.createItem(user.id(), request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(item.id())
                .toUri();
                
        return ResponseEntity.created(location).body(item);
    }

    @Operation(summary = "Оновити товар", description = "Редагує інформацію про товар. Доступно власнику або адміністратору.")
    @PutMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long itemId,
            @CurrentUser UserPrincipalDto user,
            @Valid @RequestBody ItemRequest request
    ) {
        return ResponseEntity.ok(itemService.updateItem(itemId, user.id(), request));
    }

    @Operation(summary = "Видалити товар", description = "Видаляє товар з системи. Доступно власнику або адміністратору.")
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

    @Operation(summary = "Верифікувати товар (Адмін)", description = "Встановлює статус перевірки (verified) для товару.")
    @PatchMapping("/{itemId}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> setItemVerification(
            @PathVariable Long itemId,
            @RequestParam boolean verified
    ) {
        return ResponseEntity.ok(itemService.setItemVerified(itemId, verified));
    }

    @Operation(summary = "Завантажити фото товару", description = "Додає фотографію до товару. Можна вказати, чи є вона головною.")
    @PostMapping("/{itemId}/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ItemImageResponse> uploadItemImage(
            @PathVariable Long itemId,
            @CurrentUser UserPrincipalDto user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain
    ) {
        return ResponseEntity.ok(itemImageService.uploadItemImage(itemId, user.id(), file, isMain));
    }

    @Operation(summary = "Видалити фото товару", description = "Видаляє конкретну фотографію товару.")
    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteItemImage(
            @PathVariable Long imageId,
            @CurrentUser UserPrincipalDto user
    ) {
        itemImageService.deleteItemImage(imageId, user.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Зробити фото головним", description = "Встановлює вибране фото як головне (обкладинка) для товару.")
    @PutMapping("/images/{imageId}/main")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> setMainItemImage(
            @PathVariable Long imageId,
            @CurrentUser UserPrincipalDto user
    ) {
        itemImageService.setMainItemImage(imageId, user.id());
        return ResponseEntity.ok().build();
    }
}
