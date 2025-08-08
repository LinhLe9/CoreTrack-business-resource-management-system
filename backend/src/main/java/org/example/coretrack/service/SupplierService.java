package org.example.coretrack.service;

import java.util.List;

import org.example.coretrack.dto.supplier.AddSupplierRequest;
import org.example.coretrack.dto.supplier.AddSupplierResponse;
import org.example.coretrack.dto.supplier.AllSupplierSearchResponse;
import org.example.coretrack.dto.supplier.SearchSupplierResponse;
import org.example.coretrack.dto.supplier.SupplierDetailResponse;
import org.example.coretrack.model.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    // to filter the supplier that meet the criteria
    Page<SearchSupplierResponse> findSupplier(
            String search,
            List<String> countries,
            Pageable pageable,
            User currentUser);

    // to return all supplier while search
    List<AllSupplierSearchResponse> getAllSuppliersForAutocomplete(String search, User currentUser);

    // to return all products
    Page<SearchSupplierResponse> findAllSuppliers (Pageable pageable, User currentUser);

    List<String> getAllCountries(User currentUser);

    // to add a new product to product catalog
    AddSupplierResponse createSupplier(AddSupplierRequest addSupplierRequest, User createdByUser);
  
    SupplierDetailResponse getSupplierById(Long id, User currentUser);
}
