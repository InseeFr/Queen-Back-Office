#!/bin/bash
set -e

echo "Initializing remote data..."

# Execute the SQL script
export PGPASSWORD=queenremote
psql -h queen-db-remote -U queenremote -d queen-remote <<EOF
-- Campaign
INSERT INTO public.campaign(id, label, sensitivity) VALUES
  ('SIMPSONS2020X00', 'Survey on the Simpsons tv show 2020', 'NORMAL')
ON CONFLICT (id) DO NOTHING;

-- Questionnaire model
INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES
  ('simpsons', 'Questionnaire about the Simpsons tv show', '{"id": "simpsons", "label": "Questionnaire SIMPSONS"}', 'SIMPSONS2020X00')
ON CONFLICT (id) DO NOTHING;

-- Interrogation for synchronisation test
INSERT INTO public.interrogation
(id, survey_unit_id, campaign_id, questionnaire_model_id, correlation_id)
VALUES
    ('sync-test-001', 'survey-unit-remote-01', 'SIMPSONS2020X00', 'simpsons', null)
ON CONFLICT (id) DO NOTHING;

-- Data for the interrogation
INSERT INTO public.data(id, interrogation_id, value)
VALUES (gen_random_uuid(), 'sync-test-001', '{"EXTERNAL": {"REMOTE_DATA": "synchronized from remote"}}')
ON CONFLICT (interrogation_id) DO NOTHING;

-- Personalization
INSERT INTO public.personalization(id, interrogation_id, value)
VALUES (gen_random_uuid(), 'sync-test-001', '[]')
ON CONFLICT (interrogation_id) DO NOTHING;

-- Comment
INSERT INTO public.comment(id, interrogation_id, value)
VALUES (gen_random_uuid(), 'sync-test-001', '{"COMMENT": "remote comment"}')
ON CONFLICT (interrogation_id) DO NOTHING;

-- State data
INSERT INTO public.state_data(id, interrogation_id, state, current_page, date)
VALUES (gen_random_uuid(), 'sync-test-001', 'INIT', '1', 1700000000)
ON CONFLICT (interrogation_id) DO NOTHING;
EOF

echo "Remote data initialization complete!"
