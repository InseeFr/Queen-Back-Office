-- Setup test data for MULTIMODE_MOVED integration tests
-- Clean up events tables
DELETE FROM outbox;
DELETE FROM inbox;

-- Clean up existing test interrogations
DELETE FROM state_data WHERE interrogation_id IN ('MOVED-001', 'MOVED-NEW-001');
DELETE FROM interrogation WHERE id IN ('MOVED-001', 'MOVED-NEW-001');

-- Insert test campaign if not exists
INSERT INTO campaign(id, label, sensitivity)
VALUES ('TEST-CAMPAIGN', 'Test Campaign for Integration Tests', 'NORMAL')
ON CONFLICT (id) DO NOTHING;

-- Insert test questionnaire model if not exists
INSERT INTO questionnaire_model(id, label, value, campaign_id)
VALUES ('test-questionnaire', 'Test Questionnaire', '[]'::jsonb, 'TEST-CAMPAIGN')
ON CONFLICT (id) DO NOTHING;

-- Insert test interrogations
INSERT INTO interrogation(id, survey_unit_id, campaign_id, questionnaire_model_id, correlation_id)
VALUES
    ('MOVED-001', 'SU-MOVED-001', 'TEST-CAMPAIGN', 'test-questionnaire', null),
    ('MOVED-NEW-001', 'SU-MOVED-NEW-001', 'TEST-CAMPAIGN', 'test-questionnaire', null);

-- Insert initial state data for MOVED-001 (with INIT state and page 1)
INSERT INTO state_data(id, current_page, date, state, interrogation_id)
VALUES (gen_random_uuid(), '1', extract(epoch from now()) * 1000, 'INIT', 'MOVED-001');