package nl.orangeflamingo.voornameninliedjesbackend.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import javax.sql.DataSource


@Configuration
@EnableJdbcRepositories(basePackages = ["nl.orangeflamingo.voornameninliedjesbackend.repository.postgres"])
class DatasourceConfig {

    @Bean
    @Primary
    fun dataSource(
        applicationDatasourceProperties: ApplicationDatasourceProperties
    ): DataSource {
        val config = HikariConfig()
        config.username = applicationDatasourceProperties.username
        config.password = applicationDatasourceProperties.password
        config.jdbcUrl = applicationDatasourceProperties.url
        config.schema = applicationDatasourceProperties.schema
        config.maximumPoolSize = 10
        config.poolName = applicationDatasourceProperties.poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }

    @Bean
    @FlywayDataSource
    fun migrationDataSource(
        migrationDatasourceProperties: MigrationDatasourceProperties
    ): DataSource {
        val config = HikariConfig()
        config.username = migrationDatasourceProperties.username
        config.password = migrationDatasourceProperties.password
        config.jdbcUrl = migrationDatasourceProperties.url
        config.schema = migrationDatasourceProperties.schema
        config.maximumPoolSize = 10
        config.poolName = migrationDatasourceProperties.poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }
}

@Configuration
@ConfigurationProperties(prefix = "voornameninliedjes.datasource.application")
class ApplicationDatasourceProperties {
    lateinit var username: String
    lateinit var password: String
    lateinit var url: String
    lateinit var schema: String
    lateinit var poolName: String
}

@Configuration
@ConfigurationProperties(prefix = "voornameninliedjes.datasource.migration")
class MigrationDatasourceProperties {
    lateinit var username: String
    lateinit var password: String
    lateinit var url: String
    lateinit var schema: String
    lateinit var poolName: String
}