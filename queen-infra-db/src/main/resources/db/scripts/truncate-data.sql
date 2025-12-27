--changeset davdarras:empty-data context:test

TRUNCATE TABLE public.leaf_state, public.interrogation_temp_zone, public.state_data, public.paradata_event,
    public.data, public.comment, public.personalization, public.interrogation,
    public.required_nomenclature, public.questionnaire_model, public.nomenclature,
    public.metadata, public.campaign;