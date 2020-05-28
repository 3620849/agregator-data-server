package com.weiss.weissdata.config;

import com.fasterxml.jackson.databind.util.Converter;
import com.mongodb.Mongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.internal.MongoClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

@EnableEurekaClient
@EnableMongoRepositories(basePackages = "com.weiss.weissdata.repository")
@Configuration
public class AppConfig {

}
//@EnableMongoRepositories(basePackages = "com.weiss.weissdata.repository")
//@Configuration
//public class AppConfig extends AbstractMongoClientConfiguration {
//
//    @Override
//    protected String getDatabaseName() {
//        return "test";
//    }
//
//    @Override
//    public MongoClient mongoClient() {
//        return MongoClients.create("mongodb://localhost:27017/?replicaSet=rs0&w=majority");
//    }
//    @Bean
//    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }
//
//}
