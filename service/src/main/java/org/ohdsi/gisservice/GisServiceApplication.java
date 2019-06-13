package org.ohdsi.gisservice;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class,
        basePackages = {"org.ohdsi.gisservice.*"}
)
@EnableFeignClients
public class GisServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(GisServiceApplication.class, args);
    }

}
