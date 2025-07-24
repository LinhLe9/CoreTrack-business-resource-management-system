package org.example.coretrack.dto.product;

import org.example.coretrack.model.product.ProductGroup;

public class ProductGroupResponse {
    private Long id;
    private String name;
    
    public ProductGroupResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductGroupResponse(){
    }

    public ProductGroupResponse(ProductGroup pg){
        this.id = pg.getId();
        this.name = pg.getName();
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
