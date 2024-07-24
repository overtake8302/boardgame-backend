package com.elice.boardgame.game.config;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsConfig {

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.of("ap-northeast-2"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}

