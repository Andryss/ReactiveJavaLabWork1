package ru.itmo.spaceships.config;

import javax.sql.DataSource;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class R2dbcTestConfig {

    @Bean
    public EmbeddedPostgres embeddedPostgres() throws Exception {
        return EmbeddedPostgres.builder()
                .setPort(5432) // assign random port
                .start();
    }

    @Bean
    public DataSource liquibaseDataSource(EmbeddedPostgres pg) {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(Driver.class);
        ds.setUrl(pg.getJdbcUrl("postgres", "postgres"));
        ds.setUsername("postgres");
        ds.setPassword("postgres");
        return ds;
    }

    @Bean
    public ConnectionFactory connectionFactory(EmbeddedPostgres postgres) {
        int port = postgres.getPort();
        String db = "postgres";
        String user = "postgres";
        String password = "postgres";

        PostgresqlConnectionConfiguration config =
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .port(port)
                        .username(user)
                        .password(password)
                        .database(db)
                        .build();

        return new PostgresqlConnectionFactory(config);
    }
}
