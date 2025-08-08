package org.example.coretrack.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.example.coretrack.dto.supplier.AddSupplierRequest;
import org.example.coretrack.dto.supplier.AddSupplierResponse;
import org.example.coretrack.dto.supplier.AllSupplierSearchResponse;
import org.example.coretrack.dto.supplier.SearchSupplierResponse;
import org.example.coretrack.dto.supplier.SupplierDetailResponse;
import org.example.coretrack.dto.supplier.SupplierMaterialResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.supplier.Supplier;
import org.example.coretrack.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SupplierServiceImpl implements SupplierService{
    @Autowired 
    private SupplierRepository supplierRepository;    

    /**
     * Search and filter by param.
     * @param search search + recommend drop down
     * @param countries filter by group
     * @return Page<SearchProductResponse>
     */
    @Override
    public Page<SearchSupplierResponse> findSupplier(
        String search, 
        List<String> countries,
        Pageable pageable,
        User currentUser) {

        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        List<String> processedCountries = CollectionUtils.isEmpty(countries) ? null : countries;

        Page<Supplier> suppliers = supplierRepository.findByCriteriaAndCompany(
            processedSearch,
            processedCountries,
            currentUser.getCompany(),
            pageable
        );

        return suppliers.map(SearchSupplierResponse::new);
    }

    @Override
    public List<AllSupplierSearchResponse> getAllSuppliersForAutocomplete(String search, User currentUser) {
        return supplierRepository.findBySearchKeywordAndCompany(search, currentUser.getCompany()).stream()
                    .map(AllSupplierSearchResponse::new)
                    .collect(Collectors.toList());
    }

    @Override
    public Page<SearchSupplierResponse> findAllSuppliers (Pageable pageable, User currentUser){
        Page<Supplier> suppliers = supplierRepository.findAllActiveByCompany(currentUser.getCompany(), pageable);
        return suppliers.map(SearchSupplierResponse::new);
    }

    @Override
    public List<String> getAllCountries(User currentUser){
        return supplierRepository.findDistinctCountriesByCompany(currentUser.getCompany());
    }

    @Override
    public AddSupplierResponse createSupplier(AddSupplierRequest addSupplierRequest, User createdByUser){
        // Check if createdByUser is null
        if (createdByUser == null) {
            throw new IllegalArgumentException("User information is required to create supplier");
        }
        
        // Only check phone uniqueness if phone is provided
        if (addSupplierRequest.getPhone() != null && !addSupplierRequest.getPhone().trim().isEmpty()) {
            if (supplierRepository.findByPhone(addSupplierRequest.getPhone()).isPresent()) { 
                throw new IllegalArgumentException("Phone number already exists: " + addSupplierRequest.getPhone());
            }
        }
        
        Supplier newSupplier = new Supplier(
            addSupplierRequest.getName(),
            addSupplierRequest.getContactPerson(),
            addSupplierRequest.getEmail(),
            addSupplierRequest.getPhone(),
            addSupplierRequest.getCurrency(),
            createdByUser,
            createdByUser.getCompany()
        );
        
        // Set additional fields
        newSupplier.setAddress(addSupplierRequest.getAddress());
        newSupplier.setCity(addSupplierRequest.getCity());
        newSupplier.setCountry(addSupplierRequest.getCountry());
        newSupplier.setWebsite(addSupplierRequest.getWebsite());
        
        supplierRepository.save(newSupplier);
        return new AddSupplierResponse(newSupplier);
    }

    @Override
    public SupplierDetailResponse getSupplierById(Long id, User currentUser) {
    Supplier supplier = supplierRepository.findByIdAndCompany(id, currentUser.getCompany())
            .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

    List<SupplierMaterialResponse> supplierMaterial = supplier.getMaterialSuppliers() != null
            ? supplier.getMaterialSuppliers().stream()
                .map(material -> new SupplierMaterialResponse(material))
                .collect(Collectors.toList())
            : Collections.emptyList();

    return new SupplierDetailResponse(
        supplier,
        supplierMaterial
    );}
}
