CREATE
OR REPLACE FUNCTION house_owner_change_trigger()
RETURNS TRIGGER
AS $$
BEGIN
    IF
(TG_OP = 'INSERT' OR NEW.person_id IS DISTINCT FROM OLD.person_id) THEN
        INSERT INTO house_history (house_id, person_id, date, type)
        VALUES (NEW.house_id, NEW.person_id, current_timestamp, 'OWNER');
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER house_owner_change
    AFTER INSERT OR
UPDATE ON house_owners
    FOR EACH ROW
    EXECUTE FUNCTION house_owner_change_trigger();

