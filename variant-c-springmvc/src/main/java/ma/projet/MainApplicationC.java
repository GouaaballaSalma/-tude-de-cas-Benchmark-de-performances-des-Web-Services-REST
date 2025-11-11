package ma.projet;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ma.projet"})
public class MainApplicationC {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationC.class, args);
    }
}
