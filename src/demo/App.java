package demo;

import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) {

        String[] newArgs = new String[]{
            "--server.port=8081",
            "--server.error.include-message=always",
            "--spring.main.banner-mode=off",
            "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "--spring.datasource.username=sa",
            "--spring.datasource.url=jdbc:h2:mem:testdb",
            "--spring.datasource.driverClassName=org.h2.Driver",
            "--spring.datasource.password=",
            "--spring.mvc.throw-exception-if-no-handler-found=true",
            "--spring.web.resources.add-mappings=false",
            "--app.default-size-page=10"
        };
        
        SpringApplication.run(
            App.class, 
            Stream.concat(Stream.of(args), Stream.of(newArgs)).toArray(String[]::new)
        );
    }
}
