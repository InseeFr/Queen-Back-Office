-- Ajout de la colonne interrogation_id à la table survey_unit
ALTER TABLE survey_unit
ADD COLUMN interrogation_id UUID;