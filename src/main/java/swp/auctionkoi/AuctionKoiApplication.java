package swp.auctionkoi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "swp.auctionkoi")
public class AuctionKoiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionKoiApplication.class, args);
    }

}
