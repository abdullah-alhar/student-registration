package com.example.Student_Course_Registration_System.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Creates the database connections from environment variables.
 *
 * Environment variables (exactly the names Neon uses):
 *   DATABASE_URL           pooled connection  (host has "-pooler")  -> normal app traffic
 *   DATABASE_URL_UNPOOLED  direct connection  (no "-pooler")        -> creating tables, one-time import
 * If DATABASE_URL_UNPOOLED is missing, DATABASE_URL is used for everything.
 *
 * Each value can be the connection string exactly as Neon shows it
 *   postgresql://user:password@ep-xxx.aws.neon.tech/neondb?sslmode=require&channel_binding=require
 * or a JDBC URL (then also set DB_USERNAME and DB_PASSWORD)
 *   jdbc:postgresql://localhost:5432/student_registration
 *
 * Nothing secret is written in the code. Locally the values come from .env / .env.local;
 * on Render they come from the "Environment" settings page.
 */
@Configuration
public class DatabaseConfig {

    /** The connection pool the whole app uses. */
    @Bean
    public HikariDataSource dataSource(Environment env) {
        // 1. Make sure all tables exist (schema.sql), over the DIRECT connection.
        createTablesIfMissing(env);

        // 2. Normal traffic goes over the POOLED connection.
        ConnectionInfo info = connectionInfo(env, "DATABASE_URL");
        HikariConfig config = new HikariConfig();
        config.setPoolName("app-db");
        config.setJdbcUrl(info.jdbcUrl());
        if (info.username() != null) config.setUsername(info.username());
        if (info.password() != null) config.setPassword(info.password());

        // Small pool: Render free has 512 MB RAM.
        config.setMaximumPoolSize(env.getProperty("DB_POOL_SIZE", Integer.class, 5));
        // Close idle connections so Neon can "scale to zero" (sleep) when nobody uses the app.
        config.setMinimumIdle(0);
        config.setIdleTimeout(120_000);
        // Neon may need a moment to wake up, so wait a bit longer than usual.
        config.setConnectionTimeout(30_000);
        return new HikariDataSource(config);
    }

    /** A simple, non-pooled connection to the DIRECT database URL. */
    public static DataSource directDataSource(Environment env) {
        String name = hasValue(env, "DATABASE_URL_UNPOOLED") ? "DATABASE_URL_UNPOOLED" : "DATABASE_URL";
        ConnectionInfo info = connectionInfo(env, name);
        DriverManagerDataSource ds = new DriverManagerDataSource(info.jdbcUrl());
        ds.setDriverClassName("org.postgresql.Driver");
        if (info.username() != null) ds.setUsername(info.username());
        if (info.password() != null) ds.setPassword(info.password());
        return ds;
    }

    /** Runs schema.sql. Every statement uses "IF NOT EXISTS", so existing data is never touched. */
    private static void createTablesIfMissing(Environment env) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(directDataSource(env));
    }

    // ------------------------------------------------------------------ parsing

    record ConnectionInfo(String jdbcUrl, String username, String password) {}

    private static ConnectionInfo connectionInfo(Environment env, String variableName) {
        if (!hasValue(env, variableName)) {
            throw new IllegalStateException(variableName + " is not set. Copy .env.example to .env and "
                    + "fill it in (or set it in Render > Environment).");
        }
        return parse(clean(env.getProperty(variableName)),
                clean(env.getProperty("DB_USERNAME")), clean(env.getProperty("DB_PASSWORD")));
    }

    static ConnectionInfo parse(String url, String username, String password) {
        if (url.startsWith("jdbc:")) {
            return new ConnectionInfo(url, blankToNull(username), blankToNull(password));
        }
        if (!url.startsWith("postgres://") && !url.startsWith("postgresql://")) {
            throw new IllegalStateException(
                    "Database URL must start with postgresql://, postgres:// or jdbc:postgresql://");
        }

        URI uri = URI.create(url);
        String user = username;
        String pass = password;
        if (uri.getRawUserInfo() != null) {
            String[] parts = uri.getRawUserInfo().split(":", 2);
            user = decode(parts[0]);
            if (parts.length == 2) pass = decode(parts[1]);
        }

        StringBuilder jdbc = new StringBuilder("jdbc:postgresql://").append(uri.getHost());
        if (uri.getPort() != -1) jdbc.append(':').append(uri.getPort());
        jdbc.append(uri.getRawPath() == null || uri.getRawPath().isEmpty() ? "/" : uri.getRawPath());

        if (uri.getRawQuery() != null && !uri.getRawQuery().isEmpty()) {
            // The Java driver spells this option "channelBinding" instead of "channel_binding".
            jdbc.append('?').append(uri.getRawQuery().replace("channel_binding=", "channelBinding="));
        }
        return new ConnectionInfo(jdbc.toString(), blankToNull(user), blankToNull(pass));
    }

    private static boolean hasValue(Environment env, String name) {
        String value = clean(env.getProperty(name));
        return value != null && !value.isEmpty();
    }

    /** Removes spaces and the quotes that .env files often put around values. */
    private static String clean(String value) {
        if (value == null) return null;
        String v = value.trim();
        if (v.length() >= 2 && ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'")))) {
            v = v.substring(1, v.length() - 1);
        }
        return v;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
