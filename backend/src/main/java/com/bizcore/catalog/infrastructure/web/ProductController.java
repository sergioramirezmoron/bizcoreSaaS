package com.bizcore.catalog.infrastructure.web;

import com.bizcore.catalog.application.dto.*;
import com.bizcore.catalog.domain.port.in.*;
import com.bizcore.shared.response.ApiResponse;
import com.bizcore.shared.response.PageResponse;
import com.bizcore.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog/products")
@RequiredArgsConstructor
@Tag(name = "Catalog - Products", description = "Gestión de productos, imágenes y variantes")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ListProductsUseCase listProductsUseCase;
    private final GetProductUseCase getProductUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final AddProductImageUseCase addProductImageUseCase;
    private final DeleteProductImageUseCase deleteProductImageUseCase;
    private final CreateProductVariantUseCase createProductVariantUseCase;
    private final UpdateProductVariantUseCase updateProductVariantUseCase;
    private final DeleteProductVariantUseCase deleteProductVariantUseCase;

    // ─── Products ────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar productos (paginado, con filtros)")
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        var result = listProductsUseCase.list(TenantContext.getTenantId(), categoryId, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto con imágenes y variantes")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                getProductUseCase.get(TenantContext.getTenantId(), id)));
    }

    @PostMapping
    @Operation(summary = "Crear producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse response = createProductUseCase.create(TenantContext.getTenantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                updateProductUseCase.update(TenantContext.getTenantId(), id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        deleteProductUseCase.delete(TenantContext.getTenantId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado"));
    }

    // ─── Images ──────────────────────────────────────────────────────────────

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen de producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductImageResponse>> addImage(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        ProductImageResponse response = addProductImageUseCase.add(
                TenantContext.getTenantId(),
                id,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(summary = "Eliminar imagen de producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable UUID id,
            @PathVariable UUID imageId
    ) {
        deleteProductImageUseCase.delete(TenantContext.getTenantId(), imageId);
        return ResponseEntity.ok(ApiResponse.ok("Imagen eliminada"));
    }

    // ─── Variants ────────────────────────────────────────────────────────────

    @PostMapping("/{id}/variants")
    @Operation(summary = "Crear variante de producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createVariant(
            @PathVariable UUID id,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        ProductVariantResponse response = createProductVariantUseCase.create(
                TenantContext.getTenantId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Actualizar variante de producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateVariant(
            @PathVariable UUID id,
            @PathVariable UUID variantId,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                updateProductVariantUseCase.update(TenantContext.getTenantId(), variantId, request)));
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Eliminar variante de producto")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable UUID id,
            @PathVariable UUID variantId
    ) {
        deleteProductVariantUseCase.delete(TenantContext.getTenantId(), variantId);
        return ResponseEntity.ok(ApiResponse.ok("Variante eliminada"));
    }
}
