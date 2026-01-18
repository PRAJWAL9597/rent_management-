-- 1. Make common_area_unit nullable since we now calculate it dynamically
ALTER TABLE pricing_policy ALTER COLUMN common_area_unit DROP NOT NULL;

-- 2. Ensure all core tables have the 'active' column for soft-deletes
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pricing_policy' AND column_name='active') THEN
        ALTER TABLE pricing_policy ADD COLUMN active BOOLEAN DEFAULT TRUE;
    END IF;
END $$;

-- 3. Add audit timestamps if they are missing from previous versions
ALTER TABLE pricing_policy ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE pricing_policy ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- 4. Clean up any orphaned records (Optional safety check)
UPDATE pricing_policy SET active = TRUE WHERE active IS NULL;