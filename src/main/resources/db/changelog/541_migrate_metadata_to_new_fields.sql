--liquibase formatted sql

--changeset davdarras:541-1
update campaign
    set metadata = m.value
    from metadata m
    where campaign.id = m.campaign_id;
