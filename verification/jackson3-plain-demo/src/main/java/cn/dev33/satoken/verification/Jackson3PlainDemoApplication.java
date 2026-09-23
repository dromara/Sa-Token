package cn.dev33.satoken.verification;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Jackson3PlainDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Jackson3PlainDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner verifySpiInstallation() {
        return args -> {
            System.out.println("VERIFY spi.template=" + SaManager.getSaJsonTemplate().getClass().getName());
            System.out.println("VERIFY spi.sessionType=" + SaStrategy.instance.sessionClassType.getName());
            System.out.println("VERIFY spi.createdSession=" + SaStrategy.instance.createSession.apply("startup-check").getClass().getName());
        };
    }

}
