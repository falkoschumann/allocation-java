package de.muspellheim.allocation.integration;

import de.muspellheim.allocation.domain.Batch;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@TestConfiguration
public class TestDbConfiguration {
  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

  @Bean
  public JpaProperties jpaProperties() {
    JpaProperties properties = new JpaProperties();
    properties.setGenerateDdl(true);
    properties.setShowSql(true);
    return properties;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    DataSource dataSource, JpaProperties jpaProperties) {
    var builder = createEntityManagerFactoryBuilder(jpaProperties);
    return builder.dataSource(dataSource).packages(Batch.class).persistenceUnit("testdb").build();
  }

  private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
    JpaProperties jpaProperties) {
    var jpaVendorAdapter = createJpaVendorAdapter(jpaProperties);
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
  }

  private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
    var adapter = new HibernateJpaVendorAdapter();
    adapter.setShowSql(jpaProperties.isShowSql());
    adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
    return adapter;
  }
}
