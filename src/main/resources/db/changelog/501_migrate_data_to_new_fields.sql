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

--changeset davdarras:510-2
update paradata_event
set survey_unit_id = p2.value->>'idSU'
from paradata_event p2
where paradata_event.id = p2.id;