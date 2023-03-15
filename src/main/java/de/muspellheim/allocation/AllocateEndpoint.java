package de.muspellheim.allocation;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AllocateEndpoint {
  private final EntityManagerFactory entityManagerFactory;

  public AllocateEndpoint(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @PostMapping(path = "/allocate", consumes = "application/json", produces = "application/json")
  public String post(@RequestBody OrderLine line, HttpServletResponse response) {
    var entityManager = entityManagerFactory.createEntityManager();
    var repo = new JpaRepository(entityManager);
    try {
      var batchref = Services.allocate(line, repo, entityManager);
      response.setStatus(HttpServletResponse.SC_CREATED);
      return """
        {"batchref": "%1$s"}
        """.formatted(batchref);
    } catch (OutOfStock | InvalidSku e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return """
        {"message": "%1$s"}
        """.formatted(e.getMessage());
    }
  }
}
