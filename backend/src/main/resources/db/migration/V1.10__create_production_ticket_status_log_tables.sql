-- Create production_ticket_status_log table
CREATE TABLE IF NOT EXISTS production_ticket_status_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    productionTicket_id BIGINT NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    old_status VARCHAR(50) NOT NULL,
    note TEXT,
    updatedAt DATETIME,
    updated_by_user_id BIGINT,
    FOREIGN KEY (productionTicket_id) REFERENCES production_ticket(id),
    FOREIGN KEY (updated_by_user_id) REFERENCES user(id)
);

-- Create production_ticket_detail_status_log table
CREATE TABLE IF NOT EXISTS production_ticket_detail_status_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    productionTicketDetail_id BIGINT NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    old_status VARCHAR(50) NOT NULL,
    note TEXT,
    updatedAt DATETIME,
    updated_by_user_id BIGINT,
    FOREIGN KEY (productionTicketDetail_id) REFERENCES production_ticket_detail(id),
    FOREIGN KEY (updated_by_user_id) REFERENCES user(id)
); 