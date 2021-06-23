import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
@ComponentScan(basePackages = {"controller", "service", "repository"})
public class SpringBootMainClass implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMainClass.class, args);
    }

    @Bean(name = "neo4j")
    public Connection connection() throws SQLException {
        String jdbcUrl = Optional.ofNullable(System.getenv("JDBC_DATABASE_URL")).orElse("jdbc:neo4j:bolt://localhost:7687/?user=neo4j,password=password,scheme=basic");
        Connection connection = DriverManager.getConnection(jdbcUrl);
        connection.setAutoCommit(false);
        return connection;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Tree structure API")
                .version("1.0")
                .contact(new Contact("Tomas Hamsa", "https://www.linkedin.com/in/tomas-hamsa/", "okularek@centrum.cz"))
                .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8000")
                .allowCredentials(true)
                .allowedMethods("HEAD", "GET", "POST", "PUT", "OPTIONS")
                .allowedHeaders("*");
    }
}
