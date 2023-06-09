package de.muspellheim.allocation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("allocation")
public class Config {
  private String apiUrl;
}
