--liquibase formatted sql

--changeset davdarras:510-1
delete from paradata_event p
where
    p.value->>'idSU' IS NOT NULL
    and not exists(
        select s.id
            from survey_unit s
            where s.id=p.value->>'idSU'
    );