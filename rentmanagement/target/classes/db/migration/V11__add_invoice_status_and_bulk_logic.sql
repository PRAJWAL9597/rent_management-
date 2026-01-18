
ALTER TABLE invoice 
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'GENERATED';


CREATE INDEX idx_invoice_status ON invoice(status);
CREATE INDEX idx_invoice_month ON invoice(billing_month);