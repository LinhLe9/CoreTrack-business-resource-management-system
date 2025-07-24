package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.material.AddMaterialRequest;
import org.example.coretrack.dto.material.AddMaterialResponse;
import org.example.coretrack.dto.material.AllMaterialSearchResponse;
import org.example.coretrack.dto.material.MaterialDetailResponse;
import org.example.coretrack.dto.material.MaterialGroupResponse;
import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.example.coretrack.model.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialService {
    // to filter the material that meet the criteria
    Page<SearchMaterialResponse> findMaterial(
            String search,
            List<String> groupMaterials,
            List<String> statuses,
            Pageable pageable);

    // to return all products matching with search
    List<AllMaterialSearchResponse> getAllMaterialsForAutocomplete(String search);
    
    // to return all product
    Page<SearchMaterialResponse> findAllMaterials (Pageable pageable);

    // to add a material
    AddMaterialResponse createMaterial(AddMaterialRequest request, User createdByUser);

    // to rechieve the detailed material info by id
    MaterialDetailResponse getMaterialById(Long id); 

    List<MaterialGroupResponse> getAllGroupName();
}
