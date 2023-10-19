--liquibase formatted sql

--changeset davdarras:532-1
update survey_unit
    set state = sd.state,
        state_date = sd.date,
        state_current_page = sd.current_page
    from state_data sd
    where survey_unit.id = sd.survey_unit_id;
