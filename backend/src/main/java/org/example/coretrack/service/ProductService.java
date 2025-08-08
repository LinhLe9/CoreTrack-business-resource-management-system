package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.product.AddProductRequest;
import org.example.coretrack.dto.product.AddProductResponse;
import org.example.coretrack.dto.product.AllProductSearchResponse;
import org.example.coretrack.dto.product.BOMItemResponse;
import org.example.coretrack.dto.product.ChangeProductStatusRequest;
import org.example.coretrack.dto.product.ChangeProductStatusResponse;
import org.example.coretrack.dto.product.ProductDetailResponse;
import org.example.coretrack.dto.product.ProductGroupResponse;
import org.example.coretrack.dto.product.ProductStatusTransitionResponse;
import org.example.coretrack.dto.product.ProductVariantAutoCompleteResponse;
import org.example.coretrack.dto.product.SearchProductResponse;
import org.example.coretrack.dto.product.UpdateProductRequest;
import org.example.coretrack.dto.product.UpdateProductResponse;
import org.example.coretrack.dto.product.DeleteProductResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    // to add a new product to product catalog
    AddProductResponse createProduct(AddProductRequest addProductRequest, User createdByUser);
    
    // to filter the product that meet the criteria (with company context)
    Page<SearchProductResponse> findProduct(
            String search,
            List<String> groupProducts,
            List<String> statuses,
            Pageable pageable,
            User currentUser);

    // to return all products (with company context)
    Page<SearchProductResponse> findAllProducts(Pageable pageable, User currentUser);

    // to return all product while searching (with company context)
    List<AllProductSearchResponse> getAllProductsForAutocomplete(String search, User currentUser);

    // to find product by ID (with company context)
    ProductDetailResponse getProductById(Long id, User currentUser);

    // to get all product groups (with company context)
    List<ProductGroupResponse> getAllGroupName(User currentUser);

    // to update a product (with company context)
    UpdateProductResponse updateProduct(Long id, UpdateProductRequest request, User updatedByUser);
    
    // to change product status (with company context)
    ChangeProductStatusResponse changeProductStatus(Long productId, ChangeProductStatusRequest request, User changedByUser);
    
    // to get available status transitions (with company context)
    ProductStatusTransitionResponse getAvailableStatusTransitions(Long productId, User currentUser);
    
    // to return all product variants for autocomplete (with company context)
    List<ProductVariantAutoCompleteResponse> getAllProductVariantsForAutocomplete(String search, User currentUser);

    // to return BomItem (with company context)
    List<BOMItemResponse> getBomItem(Long id, Long variantId, User currentUser);
    
    // to delete a product (soft delete) (with company context)
    DeleteProductResponse deleteProduct(Long id, User deletedByUser);
}
