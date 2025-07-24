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
        Pageable pageable) {

        // Xử lý tham số search: nếu null hoặc rỗng, đặt thành null để không áp dụng điều kiện LIKE
        String processedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // Xử lý tham số groupMaterials: nếu rỗng hoặc null, đặt thành null để không áp dụng điều kiện IN
        List<String> processedCountries = CollectionUtils.isEmpty(countries) ? null : countries;

        Page<Supplier> suppliers = supplierRepository.findByCriteria(
            processedSearch,
            processedCountries,
            pageable
        );

        return suppliers.map(SearchSupplierResponse::new);
    }

    @Override
    public List<AllSupplierSearchResponse> getAllSuppliersForAutocomplete(String search) {
        return supplierRepository.findBySearchKeyword(search).stream()
                    .map(AllSupplierSearchResponse::new)
                    .collect(Collectors.toList());
    }

    @Override
    public Page<SearchSupplierResponse> findAllSuppliers (Pageable pageable){
        Page<Supplier> suppliers = supplierRepository.findAllActive(pageable);
        return suppliers.map(SearchSupplierResponse::new);
    }

    @Override
    public List<String> getAllCountries(){
        return supplierRepository.findDistinctCountries();
    }

    @Override
    public AddSupplierResponse createSupplier(AddSupplierRequest addSupplierRequest, User createdByUser){
        if (supplierRepository.findByPhone(addSupplierRequest.getPhone()).isPresent()) { 
                    throw new IllegalArgumentException("Phone number already exists: " + addSupplierRequest.getPhone());
        } else {
            Supplier newSupplier  = new Supplier(
                addSupplierRequest.getName(),
                addSupplierRequest.getContactPerson(),
                addSupplierRequest.getEmail(),
                addSupplierRequest.getPhone(),
                addSupplierRequest.getCurrency(),
                createdByUser
            );
            supplierRepository.save(newSupplier);
        return new AddSupplierResponse(newSupplier);
        }
    }

    @Override
    public SupplierDetailResponse getSupplierById(Long id) {
    Supplier supplier = supplierRepository.findById(id)
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
