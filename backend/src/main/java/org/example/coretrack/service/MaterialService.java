package org.example.coretrack.service;

import java.math.BigDecimal;
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
import org.example.coretrack.dto.material.MaterialSupplierResponse;
import org.example.coretrack.dto.material.DeleteMaterialResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.MaterialVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialService {
    // to filter the material that meet the criteria (with company context)
    Page<SearchMaterialResponse> findMaterial(
            String search,
            List<String> groupMaterials,
            List<String> statuses,
            Pageable pageable,
            User currentUser);

    // to return all products matching with search (with company context)
    List<AllMaterialSearchResponse> getAllMaterialsForAutocomplete(String search, User currentUser);
    
    // to return all material variants for autocomplete (with company context)
    List<MaterialVariantAutoCompleteResponse> getAllMaterialVariantsForAutocomplete(String search, User currentUser);
    
    // to return all product (with company context)
    Page<SearchMaterialResponse> findAllMaterials(Pageable pageable, User currentUser);

    // to add a material (with company context)
    AddMaterialResponse createMaterial(AddMaterialRequest request, User createdByUser);

    // to rechieve the detailed material info by id (with company context)
    MaterialDetailResponse getMaterialById(Long id, User currentUser); 

    // to update a material (with company context)
    UpdateMaterialResponse updateMaterial(Long id, UpdateMaterialRequest request, User updatedByUser);

    // to get all material groups (with company context)
    List<MaterialGroupResponse> getAllGroupName(User currentUser);

    // to change material status (with company context)
    ChangeMaterialStatusResponse changeMaterialStatus(Long materialId, ChangeMaterialStatusRequest request, User changedByUser);

    // to get available status transitions for a material (with company context)
    MaterialStatusTransitionResponse getAvailableStatusTransitions(Long materialId, User currentUser);

    // to get suppliers by material variant SKU (with company context)
    List<MaterialSupplierResponse> getSuppliersByMaterialVariantSku(String materialVariantSku, User currentUser);

    // to delete a material (soft delete) (with company context)
    DeleteMaterialResponse deleteMaterial(Long id, User deletedByUser);
}
