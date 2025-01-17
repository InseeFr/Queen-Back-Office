--changeset davdarras:empty-data context:test

TRUNCATE TABLE public.survey_unit_temp_zone, public.state_data, public.paradata_event,
    public.data, public.comment, public.personalization, public.survey_unit,
    public.required_nomenclature, public.questionnaire_model, public.nomenclature,
    public.metadata, public.campaign;