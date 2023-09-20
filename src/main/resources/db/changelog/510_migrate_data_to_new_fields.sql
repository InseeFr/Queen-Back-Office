--liquibase formatted sql

--changeset davdarras:1704568723481-1
delete from paradata_event p
where
    p.value->>'idSU' IS NOT NULL
    and not exists(
        select s.id
            from survey_unit s
            where s.id=p.value->>'idSU'
    );

--changeset davdarras:1704568723481-2
update paradata_event
    set survey_unit_id = p2.value->>'idSU'
    from paradata_event p2
    where paradata_event.id = p2.id;

--changeset davdarras:1704568723481-3
update survey_unit
    set comment = c.value
    from comment c
    where survey_unit.id = c.survey_unit_id;

--changeset davdarras:1704568723481-4
update survey_unit
    set personalization = p.value
    from personalization p
    where survey_unit.id = p.survey_unit_id;

--changeset davdarras:1704568723481-5
update survey_unit
    set data = d.value
    from data d
    where survey_unit.id = d.survey_unit_id;
