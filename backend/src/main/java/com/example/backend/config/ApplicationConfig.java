package com.example.backend.config;

import com.example.backend.exception.ExceptionAdvice;
import com.example.backend.repository.*;
import com.example.backend.service.CommandService;
import com.example.backend.service.ExecutionService;
import com.example.backend.web.CommandController;
import com.example.backend.web.ExecutionController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean(initMethod = "init", destroyMethod = "destroy")
    public CommandsStore kafkaStreams() {
        return new CommandsStore();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ExecutionsStore executionsStore() {
        return new ExecutionsStore();
    }

    @Bean(name = "producer", initMethod = "init", destroyMethod = "destroy")
    public KafkaProducer producer(){
        return new KafkaProducer();
    }

    @Bean
    public CommandRepositoryImpl commandRepository(CommandsStore store, KafkaProducer producer){
        return new CommandRepositoryImpl(producer, store);
    }

    @Bean
    public ExecutionRepositoryImpl executionRepository(ExecutionsStore store, KafkaProducer producer){
        return new ExecutionRepositoryImpl(producer, store);
    }

    @Bean
    public CommandService commandService(CommandRepository repository){
        return new CommandService(repository);
    }

    @Bean
    public ExecutionService executionService(CommandRepository commandRepository, ExecutionRepository executionRepository){
        return new ExecutionService(commandRepository, executionRepository);
    }

    @Bean
    public CommandController commandController(CommandService commandService){
        return new CommandController(commandService);
    }

    @Bean
    public ExecutionController executionController(ExecutionService executionService){
        return new ExecutionController(executionService);
    }

    @Bean
    public ExceptionAdvice exceptionAdvice(){
        return new ExceptionAdvice();
    }
}
