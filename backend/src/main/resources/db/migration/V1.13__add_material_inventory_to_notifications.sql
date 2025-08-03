-- Add material_inventory_id column to notifications table
ALTER TABLE notifications ADD COLUMN material_inventory_id BIGINT;

-- Add foreign key constraint
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_material_inventory 
    FOREIGN KEY (material_inventory_id) REFERENCES materialInventory(id);

-- Add index for better performance
CREATE INDEX idx_notifications_material_inventory_id ON notifications(material_inventory_id); 