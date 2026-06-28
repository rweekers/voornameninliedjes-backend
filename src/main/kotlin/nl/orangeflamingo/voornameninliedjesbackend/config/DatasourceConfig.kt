package nl.orangeflamingo.voornameninliedjesbackend.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
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
        properties: ApplicationDatasourceProperties
    ): DataSource {
        val config = HikariConfig()
        config.username = properties.username
        config.password = properties.password
        config.jdbcUrl = properties.jdbcUrl
        config.schema = properties.schema
        config.maximumPoolSize = properties.maxPoolSize
        config.minimumIdle = properties.minIdle
        config.poolName = properties.poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }

    @Bean("flywayDataSource")
    fun migrationDataSource(
        properties: MigrationDatasourceProperties
    ): DataSource {
        val config = HikariConfig()
        config.username = properties.username
        config.password = properties.password
        config.jdbcUrl = properties.jdbcUrl
        config.schema = properties.schema
        config.maximumPoolSize = 5
        config.poolName = properties.poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }
}

@ConfigurationProperties("voornameninliedjes.datasource.application")
data class ApplicationDatasourceProperties(
    val username: String,
    val password: String,
    val jdbcUrl: String,
    val schema: String,
    val poolName: String,
    val maxPoolSize: Int,
    val minIdle: Int
)

@ConfigurationProperties("voornameninliedjes.datasource.migration")
data class MigrationDatasourceProperties(
    val username: String,
    val password: String,
    val jdbcUrl: String,
    val schema: String,
    val poolName: String
)