package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.product.AddProductRequest;
import org.example.coretrack.dto.product.AddProductResponse;
import org.example.coretrack.dto.product.AllProductSearchResponse;
import org.example.coretrack.dto.product.ProductDetailResponse;
import org.example.coretrack.dto.product.ProductGroupResponse;
import org.example.coretrack.dto.product.SearchProductResponse;
import org.example.coretrack.model.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    // to add a new product to product catalog
    AddProductResponse createProduct(AddProductRequest addProductRequest, User createdByUser);
    
    // to filter the product that meet the criteria
    Page<SearchProductResponse> findProduct(
            String search,
            List<String> groupProducts,
            List<String> statuses,
            Pageable pageable);

    // to return all products
    Page<SearchProductResponse> findAllProducts (Pageable pageable);

    // to return all product while searching
    List<AllProductSearchResponse> getAllProductsForAutocomplete(String search);

    // to find product by ID
    ProductDetailResponse getProductById(Long id);

    List<ProductGroupResponse> getAllGroupName();
}
