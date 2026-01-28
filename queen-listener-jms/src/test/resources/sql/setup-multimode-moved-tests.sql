-- Setup test data for MULTIMODE_MOVED integration tests
-- Clean up events tables
DELETE FROM outbox;
DELETE FROM inbox;

-- Clean up existing test interrogations
DELETE FROM state_data WHERE interrogation_id IN ('MOVED-001', 'MOVED-NEW-001', 'MOVED-LOCKED-001', 'MOVED-UNLOCKED-001', 'INT-001', 'INT-002', 'INT-003', 'SUB-001', 'LEAF-001', 'MULTI-1', 'MULTI-2', 'MULTI-3', 'DUP-001');
DELETE FROM interrogation WHERE id IN ('MOVED-001', 'MOVED-NEW-001', 'MOVED-LOCKED-001', 'MOVED-UNLOCKED-001', 'INT-001', 'INT-002', 'INT-003', 'SUB-001', 'LEAF-001', 'MULTI-1', 'MULTI-2', 'MULTI-3', 'DUP-001');

-- Insert test campaign if not exists
INSERT INTO campaign(id, label, sensitivity)
VALUES ('TEST-CAMPAIGN', 'Test Campaign for Integration Tests', 'NORMAL')
ON CONFLICT (id) DO NOTHING;

-- Insert test questionnaire model if not exists
INSERT INTO questionnaire_model(id, label, value, campaign_id)
VALUES ('test-questionnaire', 'Test Questionnaire', '[]'::jsonb, 'TEST-CAMPAIGN')
ON CONFLICT (id) DO NOTHING;

-- Insert test interrogations
INSERT INTO interrogation(id, survey_unit_id, campaign_id, questionnaire_model_id, correlation_id, locked)
VALUES
    ('MOVED-001', 'SU-MOVED-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('MOVED-NEW-001', 'SU-MOVED-NEW-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('MOVED-LOCKED-001', 'SU-MOVED-LOCKED-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, true),
    ('MOVED-UNLOCKED-001', 'SU-MOVED-UNLOCKED-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('INT-001', 'SU-INT-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('INT-002', 'SU-INT-002', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('INT-003', 'SU-INT-003', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('SUB-001', 'SU-SUB-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('LEAF-001', 'SU-LEAF-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('MULTI-1', 'SU-MULTI-1', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('MULTI-2', 'SU-MULTI-2', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('MULTI-3', 'SU-MULTI-3', 'TEST-CAMPAIGN', 'test-questionnaire', null, false),
    ('DUP-001', 'SU-DUP-001', 'TEST-CAMPAIGN', 'test-questionnaire', null, false);

-- Insert initial state data for MOVED-001 (with INIT state and page 1)
INSERT INTO state_data(id, current_page, date, state, interrogation_id)
VALUES (gen_random_uuid(), '1', extract(epoch from now()) * 1000, 'INIT', 'MOVED-001');

-- Insert initial state data for MOVED-LOCKED-001 (locked interrogation)
INSERT INTO state_data(id, current_page, date, state, interrogation_id)
VALUES (gen_random_uuid(), '1', extract(epoch from now()) * 1000, 'INIT', 'MOVED-LOCKED-001');

-- Insert initial state data for MOVED-UNLOCKED-001 (unlocked interrogation)
INSERT INTO state_data(id, current_page, date, state, interrogation_id)
VALUES (gen_random_uuid(), '1', extract(epoch from now()) * 1000, 'INIT', 'MOVED-UNLOCKED-001');