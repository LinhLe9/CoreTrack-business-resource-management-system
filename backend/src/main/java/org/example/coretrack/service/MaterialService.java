package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.material.AddMaterialRequest;
import org.example.coretrack.dto.material.AddMaterialResponse;
import org.example.coretrack.dto.material.AllMaterialSearchResponse;
import org.example.coretrack.dto.material.MaterialDetailResponse;
import org.example.coretrack.dto.material.MaterialGroupResponse;
import org.example.coretrack.dto.material.SearchMaterialResponse;
import org.example.coretrack.dto.material.UpdateMaterialRequest;
import org.example.coretrack.dto.material.UpdateMaterialResponse;
import org.example.coretrack.dto.material.ChangeMaterialStatusRequest;
import org.example.coretrack.dto.material.ChangeMaterialStatusResponse;
import org.example.coretrack.dto.material.MaterialStatusTransitionResponse;
import org.example.coretrack.dto.material.MaterialVariantAutoCompleteResponse;
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
    
    // to return all material variants for autocomplete
    List<MaterialVariantAutoCompleteResponse> getAllMaterialVariantsForAutocomplete(String search);
    
    // to return all product
    Page<SearchMaterialResponse> findAllMaterials (Pageable pageable);

    // to add a material
    AddMaterialResponse createMaterial(AddMaterialRequest request, User createdByUser);

    // to rechieve the detailed material info by id
    MaterialDetailResponse getMaterialById(Long id); 

    // to update a material
    UpdateMaterialResponse updateMaterial(Long id, UpdateMaterialRequest request, User updatedByUser);

    List<MaterialGroupResponse> getAllGroupName();

    // to change material status
    ChangeMaterialStatusResponse changeMaterialStatus(Long materialId, ChangeMaterialStatusRequest request, User changedByUser);

    // to get available status transitions for a material
    MaterialStatusTransitionResponse getAvailableStatusTransitions(Long materialId);
}
