CREATE OR REPLACE FUNCTION person_house_change_trigger()
    RETURNS TRIGGER
AS $$
BEGIN
    IF (TG_OP = 'INSERT' OR NEW.house_id IS DISTINCT FROM OLD.house_id) THEN
        INSERT INTO house_history (house_id, person_id, date, type)
        VALUES (NEW.house_id, NEW.id, current_timestamp, 'TENANT');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER person_house_change
    AFTER INSERT OR UPDATE ON person
    FOR EACH ROW
EXECUTE FUNCTION person_house_change_trigger();
