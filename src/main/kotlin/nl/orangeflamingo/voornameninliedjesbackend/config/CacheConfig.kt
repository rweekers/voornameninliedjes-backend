package nl.orangeflamingo.voornameninliedjesbackend.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val caffeineCache = Caffeine.newBuilder()
            .recordStats()
            .maximumSize(5000)
            .expireAfterWrite(Duration.ofHours(23))

        return CaffeineCacheManager().apply {
            isAllowNullValues = false
            setCaffeine(caffeineCache)
            cacheNames = listOf("songsByPrefix")
        }
    }
}