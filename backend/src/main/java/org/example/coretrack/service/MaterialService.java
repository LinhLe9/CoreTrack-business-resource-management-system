package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialService {
// to filter the material that meet the criteria
    Page<SearchMaterialResponse> findProduct(
            String search,
            List<String> groupMaterials,
            List<String> statuses,
            Pageable pageable);

    // to return all product
    List<SearchProductResponse> getAllProductsForAutocomplete(String search);
    
}
