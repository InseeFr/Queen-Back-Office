package fr.insee.queen.application.configuration.db;

import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.gateway.NomenclatureRepository;
import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.paradata.gateway.ParadataEventRepository;
import fr.insee.queen.domain.surveyunit.gateway.StateDataRepository;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunittempzone.gateway.SurveyUnitTempZoneRepository;
import fr.insee.queen.infrastructure.mongo.paradata.dao.ParadataEventMongoDao;
import fr.insee.queen.infrastructure.mongo.questionnaire.dao.CampaignMongoDao;
import fr.insee.queen.infrastructure.mongo.questionnaire.dao.NomenclatureMongoDao;
import fr.insee.queen.infrastructure.mongo.questionnaire.dao.QuestionnaireModelMongoDao;
import fr.insee.queen.infrastructure.mongo.surveyunit.dao.StateDataMongoDao;
import fr.insee.queen.infrastructure.mongo.surveyunit.dao.SurveyUnitMongoDao;
import fr.insee.queen.infrastructure.mongo.surveyunittempzone.dao.SurveyUnitTempZoneMongoDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ConditionalOnProperty(name = "feature.mongo.enabled", havingValue = "true")
@ComponentScan(basePackages = {"fr.insee.queen.infrastructure.mongo"})
@EnableMongoRepositories(basePackages = {"fr.insee.queen.infrastructure.mongo"})
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MongoConfiguration {
    private final ParadataEventMongoDao paradataEventMongoDao;
    private final CampaignMongoDao campaignMongoDao;
    private final QuestionnaireModelMongoDao questionnaireModelDao;
    private final NomenclatureMongoDao nomenclatureMongoDao;
    private final SurveyUnitMongoDao surveyUnitMongoDao;
    private final StateDataMongoDao stateDataMongoDao;
    private final SurveyUnitTempZoneMongoDao surveyUnitTempZoneMongoDao;

    @Bean
    @Primary
    public ParadataEventRepository paradataEventRepository() {
        return paradataEventMongoDao;
    }

    @Bean
    @Primary
    public CampaignRepository campaignRepository() {
        return campaignMongoDao;
    }

    @Bean
    @Primary
    public QuestionnaireModelRepository questionnaireModelRepository() {
        return questionnaireModelDao;
    }

    @Bean
    @Primary
    public NomenclatureRepository nomenclatureRepository() {
        return nomenclatureMongoDao;
    }

    @Bean
    @Primary
    public SurveyUnitRepository surveyUnitRepository() {
        return surveyUnitMongoDao;
    }

    @Bean
    @Primary
    public StateDataRepository stateDataRepository() {
        return stateDataMongoDao;
    }

    @Bean
    @Primary
    public SurveyUnitTempZoneRepository surveyUnitTempZoneRepository() {
        return surveyUnitTempZoneMongoDao;
    }

}
