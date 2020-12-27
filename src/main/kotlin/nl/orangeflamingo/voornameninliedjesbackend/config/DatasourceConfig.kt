package nl.orangeflamingo.voornameninliedjesbackend.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
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
        @Value("\${spring.datasource.application.url}") url: String?,
        @Value("\${spring.datasource.application.username}") username: String?,
        @Value("\${spring.datasource.application.password}") password: String?,
        @Value("\${spring.datasource.application.schema}") schema: String?,
        @Value("\${spring.datasource.application.hikari.pool-name}") poolName: String?
    ): DataSource {
        val config = HikariConfig()
        config.username = username
        config.password = password
        config.jdbcUrl = url
        config.schema = schema
        config.maximumPoolSize = 10
        config.poolName = poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }

    @Bean
    @FlywayDataSource
    fun migrationDataSource(
        @Value("\${spring.datasource.migration.url}") url: String?,
        @Value("\${spring.datasource.migration.username}") username: String?,
        @Value("\${spring.datasource.migration.password}") password: String?,
        @Value("\${spring.datasource.application.schema}") schema: String?,
        @Value("\${spring.datasource.migration.hikari.pool-name}") poolName: String?
    ): DataSource {
        val config = HikariConfig()
        config.username = username
        config.password = password
        config.jdbcUrl = url
        config.schema = schema
        config.maximumPoolSize = 10
        config.poolName = poolName
        config.driverClassName = Driver::class.java.name
        config.dataSourceProperties["prepareThreshold"] = 0
        return HikariDataSource(config)
    }
}