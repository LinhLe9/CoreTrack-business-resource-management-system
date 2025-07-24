package org.example.coretrack.dto.material;

import org.example.coretrack.model.material.MaterialGroup;

public class MaterialGroupResponse {
    private Long id;
    private String name;
    
    public MaterialGroupResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MaterialGroupResponse(){
    }

    public MaterialGroupResponse(MaterialGroup pg){
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
