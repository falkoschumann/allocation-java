/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.integration;

import com.zaxxer.hikari.HikariDataSource;
import de.muspellheim.allocation.domain.Batch;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@TestConfiguration(proxyBeanMethods = false)
public class TestDbConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties("it.datasource")
  public DataSourceProperties itDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("it.datasource.configuration")
  public HikariDataSource dataSource(DataSourceProperties itDataSourceProperties) {
    return itDataSourceProperties
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  @Primary
  @ConfigurationProperties("it.jpa")
  public JpaProperties itJpaProperties() {
    return new JpaProperties();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource, JpaProperties itJpaProperties) {
    var builder = createEntityManagerFactoryBuilder(itJpaProperties);
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
