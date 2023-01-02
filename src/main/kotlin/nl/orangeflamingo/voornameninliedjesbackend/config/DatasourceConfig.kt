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
    ): DataSource =
        createDatasource(applicationDatasourceProperties)

    @Bean
    @FlywayDataSource
    fun migrationDataSource(
        migrationDatasourceProperties: MigrationDatasourceProperties
    ): DataSource =
        createDatasource(migrationDatasourceProperties)

    private fun createDatasource(properties: DatasourceProperties): DataSource {
        val config = HikariConfig()
        config.username = properties.username
        config.password = properties.password
        config.jdbcUrl = "jdbc:postgresql://${properties.host}:${properties.port}/${properties.database}?createDatabaseIfNotExist=true"
        config.schema = properties.schema
        config.maximumPoolSize = 10
        config.poolName = properties.poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }
}

@Configuration
@ConfigurationProperties(prefix = "voornameninliedjes.datasource.application")
class ApplicationDatasourceProperties : DatasourceProperties()

@Configuration
@ConfigurationProperties(prefix = "voornameninliedjes.datasource.migration")
class MigrationDatasourceProperties : DatasourceProperties()

open class DatasourceProperties {
    open lateinit var username: String
    lateinit var password: String
    lateinit var host: String
    lateinit var port: String
    lateinit var database: String
    lateinit var schema: String
    lateinit var poolName: String
}