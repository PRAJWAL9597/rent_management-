-- Step 1: Add the new columns
ALTER TABLE tenant 
ADD COLUMN joining_date DATE NOT NULL DEFAULT CURRENT_DATE;

-- Step 2: Ensure room_no is UNIQUE so two people aren't put in one room
ALTER TABLE tenant ADD CONSTRAINT uq_room_no UNIQUE (room_no);