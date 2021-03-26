package fr.insee.queen.api.configuration;

import org.bson.UuidRepresentation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import fr.insee.queen.api.helper.ApiMongoRepositoryFactoryBean;

@Configuration
@ConditionalOnProperty(prefix = "fr.insee.queen.application", name = "persistenceType", havingValue = "MONGODB", matchIfMissing = true)
@EnableMongoRepositories(
        repositoryFactoryBeanClass = ApiMongoRepositoryFactoryBean.class,
        basePackages = "fr.insee.queen.api.repository")
public class ApiMongoRepositoryConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.database}")
    private String database;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiMongoRepositoryConfig.class);

    public ApiMongoRepositoryConfig() {
        LOGGER.info("Repository Configuration: {}", ApiMongoRepositoryConfig.class);
    }

    @Bean(name = "mongoClient")
    public MongoClient mongoClient() {
    	ConnectionString cs = new ConnectionString(uri);
    	return MongoClients.create(MongoClientSettings.builder().applyConnectionString(cs)
    			.uuidRepresentation(UuidRepresentation.JAVA_LEGACY).build());    	
    }

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() {
    	MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), database);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(mongoCustomConversions()); // tell mongodb to use the custom converters
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;
    }
    
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(JsonNodeToDocumentConverter.INSTANCE);
        converters.add(DocumentToJsonNodeConverter.INSTANCE);
        converters.add(ListDocumentToJsonNodeConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

  @WritingConverter
  enum JsonNodeToDocumentConverter implements Converter<JsonNode, Object> {
      INSTANCE;

      public Object convert(JsonNode source) {
    	  
          if(source == null)
              return null;
          
          if(source.isArray()) {
        	  List<Document> returnVal = new ArrayList<>();
        	  for(JsonNode obj : source) {
        		  returnVal.add(Document.parse(obj.toString()));
        	  }
        	  return returnVal;
          } else {
        	  return Document.parse(source.toString());
          }
          
      }

  }

  @ReadingConverter
  enum DocumentToJsonNodeConverter implements Converter<Document, JsonNode> {
      INSTANCE;
	 
      public JsonNode convert(Document source) {
          if(source == null)
              return null;
          
          ObjectMapper mapper = new ObjectMapper();
          try {
        	  return mapper.readTree(source.toJson());
          } catch (IOException e) {
              throw new RuntimeException("Unable to parse DbObject to JsonNode", e);
          }
      }
  }
      
      @ReadingConverter
      enum ListDocumentToJsonNodeConverter implements Converter<List<Document>, JsonNode> {
          INSTANCE;
    	  
          public JsonNode convert(List<Document> source) {
        	  if(source == null)
                  return null;
        	  
        	  ObjectMapper mapper = new ObjectMapper();
              JsonNode returnVal = mapper.createArrayNode();
              try {
            	  return mapper.readTree("[".concat(source.stream().map(Document::toJson)
            	  .collect(Collectors.joining(",")))
            	  .concat("]"));
              } catch (JsonProcessingException e) {
              e.printStackTrace();
            } 
              return returnVal;

          }
      }

}
