package org.auslides.security;

import org.auslides.security.config.*;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * run:
 *    mvn clean package spring-boot:run
 */
@Import({AppInitializer.class,
        WebConfig.class,
        ShiroConfig.class,
        RepositoryConfig.class,
        MybatisConfig.class})
@ComponentScan(basePackages = "org.auslides.security.rest")
@SpringBootConfiguration
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(Application.class)
                .run(args);
    }
}