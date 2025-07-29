CREATE TABLE MaterialStatusAuditLog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    previous_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    reason VARCHAR(500),
    FOREIGN KEY (material_id) REFERENCES Material(id),
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Create index for better query performance
CREATE INDEX idx_material_status_audit_material_id ON MaterialStatusAuditLog(material_id);
CREATE INDEX idx_material_status_audit_changed_at ON MaterialStatusAuditLog(changed_at); 