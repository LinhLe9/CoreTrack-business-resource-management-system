CREATE TABLE ProductStatusAuditLog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    previous_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    reason VARCHAR(500),
    FOREIGN KEY (product_id) REFERENCES Product(id),
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE INDEX idx_product_status_audit_product_id ON ProductStatusAuditLog(product_id);
CREATE INDEX idx_product_status_audit_changed_at ON ProductStatusAuditLog(changed_at); 