CREATE SCHEMA IF NOT EXISTS chenup;

SET search_path TO chenup, public;


-- ============================================================================
-- THING
-- ============================================================================

create table if not exists chenup.thing
(
    id      uuid    default uuidv7() not null
        constraint thing_pk
            primary key,
    version integer default 0        not null
);

alter table chenup.thing
    owner to postgres;


-- Generic trigger functions used by tables whose rows correspond to thing rows.

create or replace function chenup.insert_thing() returns trigger
    language plpgsql
as
$$
BEGIN
    INSERT INTO chenup.thing (id)
    VALUES (NEW.id)
    ON CONFLICT DO NOTHING;

    RETURN NEW;
END;
$$;

alter function chenup.insert_thing() owner to postgres;


create or replace function chenup.delete_thing() returns trigger
    language plpgsql
as
$$
BEGIN
    DELETE FROM chenup.thing
    WHERE id = OLD.id;

    RETURN NULL; -- AFTER triggers ignore return value
END;
$$;

alter function chenup.delete_thing() owner to postgres;


create or replace function chenup.increment_thing_version() returns trigger
    language plpgsql
as
$$
BEGIN
    UPDATE chenup.thing
    SET version = version + 1
    WHERE id = NEW.id;

    RETURN NEW;
END;
$$;

alter function chenup.increment_thing_version() owner to postgres;


-- ============================================================================
-- PERSON
-- ============================================================================

create type chenup.person_name as
(
    given_name text,
    sur_name   text,
    alias      boolean
);

alter type chenup.person_name owner to postgres;


create table if not exists chenup.person
(
    id            uuid not null
        primary key
        references chenup.thing
            on delete cascade,
    name          text,
    birth_date    date,
    separate_name chenup.person_name,
    birth_instant timestamp with time zone
);

alter table chenup.person
    owner to postgres;


create trigger person_insert_thing
    before insert
    on chenup.person
    for each row
execute procedure chenup.insert_thing();

create trigger person_increment_version
    before update
    on chenup.person
    for each row
execute procedure chenup.increment_thing_version();

create trigger person_delete_thing
    after delete
    on chenup.person
    for each row
execute procedure chenup.delete_thing();


-- ============================================================================
-- ORGANIZATION
-- ============================================================================

create table if not exists chenup.organization
(
    id   uuid not null
        primary key
        references chenup.thing
            on delete cascade,
    name text
);

alter table chenup.organization
    owner to postgres;


create trigger organization_insert_thing
    before insert
    on chenup.organization
    for each row
execute procedure chenup.insert_thing();

create trigger organization_increment_version
    before update
    on chenup.organization
    for each row
execute procedure chenup.increment_thing_version();

create trigger organization_delete_thing
    after delete
    on chenup.organization
    for each row
execute procedure chenup.delete_thing();


-- ============================================================================
-- EMPLOYMENT
-- ============================================================================

create table if not exists chenup.employment
(
    employee        uuid not null
        constraint employment_person_person_fk
            references chenup.person
            on delete cascade,
    employer        uuid not null
        constraint employment_organization_id_fk
            references chenup.organization
            on delete cascade,
    id              uuid not null
        constraint employment_pk
            primary key
        references chenup.thing
            on delete cascade,
    start_date      date,
    end_date        date,
    start_date_time timestamp with time zone
);

alter table chenup.employment
    owner to postgres;


create trigger employment_insert_thing
    before insert
    on chenup.employment
    for each row
execute procedure chenup.insert_thing();

create trigger employment_increment_version
    before update
    on chenup.employment
    for each row
execute procedure chenup.increment_thing_version();

create trigger employment_delete_thing
    after delete
    on chenup.employment
    for each row
execute procedure chenup.delete_thing();
