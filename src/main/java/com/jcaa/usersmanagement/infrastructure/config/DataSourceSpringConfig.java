package com.jcaa.usersmanagement.infrastructure.config;

import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class DataSourceSpringConfig {

  private static final String PROP_DB_HOST     = "${db.host}";
  private static final String PROP_DB_PORT     = "${db.port}";
  private static final String PROP_DB_NAME     = "${db.name}";
  private static final String PROP_DB_USERNAME = "${db.username}";
  private static final String PROP_DB_PASSWORD = "${db.password}";
  private static final String PROP_DB_SSLMODE  = "${db.sslmode:require}";

  private static final String HIKARI_SSLMODE_PROPERTY = "sslmode";
  private static final String SCHEMA_SQL_PATH = "schema.sql";

  private static final String LOG_DATASOURCE_INIT =
          "[DataSourceSpringConfig] DataSource inicializado. host={} port={} sslmode={}";
  private static final String LOG_SCHEMA_INIT_SUCCESS =
          "[DataSourceSpringConfig] Esquema de base de datos verificado e inicializado.";
  private static final String LOG_SCHEMA_INIT_WARN =
          "[DataSourceSpringConfig] Aviso al inicializar esquema: {}";

  @Value(PROP_DB_HOST)
  private String dbHost;

  @Value(PROP_DB_PORT)
  private int dbPort;

  @Value(PROP_DB_NAME)
  private String dbName;

  @Value(PROP_DB_USERNAME)
  private String dbUsername;

  @Value(PROP_DB_PASSWORD)
  private String dbPassword;

  @Value(PROP_DB_SSLMODE)
  private String dbSslmode;

  @Bean
  public DataSource dataSource() {
    final DatabaseConfig config = new DatabaseConfig(dbHost, dbPort, dbName, dbUsername, dbPassword);

    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(config.buildJdbcUrl());
    hikariConfig.setUsername(config.username());
    hikariConfig.setPassword(config.password());
    hikariConfig.setMaximumPoolSize(10);
    hikariConfig.setMinimumIdle(2);
    hikariConfig.setConnectionTimeout(30_000);
    hikariConfig.addDataSourceProperty(HIKARI_SSLMODE_PROPERTY, dbSslmode);

    log.info(LOG_DATASOURCE_INIT, dbHost, dbPort, dbSslmode);
    final HikariDataSource dataSource = new HikariDataSource(hikariConfig);
    initializeSchema(dataSource);
    return dataSource;
  }

  private void initializeSchema(final DataSource dataSource) {
    try {
      final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
      populator.addScript(new ClassPathResource(SCHEMA_SQL_PATH));
      populator.setContinueOnError(true);
      populator.execute(dataSource);
      log.info(LOG_SCHEMA_INIT_SUCCESS);
    } catch (final Exception exception) {
      log.warn(LOG_SCHEMA_INIT_WARN, exception.getMessage());
    }
  }
}