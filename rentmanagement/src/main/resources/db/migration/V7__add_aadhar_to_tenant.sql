-- Migration to add Aadhar Number to Tenant table
ALTER TABLE tenant 
ADD COLUMN aadhar_no VARCHAR(12) UNIQUE;

-- UNIQUE ensures no two tenants share the same Aadhar.