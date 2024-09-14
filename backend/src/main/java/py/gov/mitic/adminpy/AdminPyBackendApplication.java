package py.gov.mitic.adminpy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "py.gov.mitic.adminpy")
@EntityScan(basePackages = "py.gov.mitic.adminpy.model.entity")
@ComponentScan(basePackages = {"py.gov.mitic.adminpy", "py.gov.mitic.adminpy.aspect"})
public class AdminPyBackendApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(AdminPyBackendApplication.class, args);
		
	}

}
