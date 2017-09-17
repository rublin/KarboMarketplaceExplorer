package org.rublin;

import org.rublin.service.OrderService;
import org.rublin.service.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:telegram.properties")
public class AppConfiguration {

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }

}
