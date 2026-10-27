package com.webizera.knowledgeassistant;

import org.springframework.boot.SpringApplication;

public class TestKnowledgeAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.from(KnowledgeAssistantApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
