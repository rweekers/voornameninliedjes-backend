package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaSongDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Profile("!integration-test")
class WikipediaHttpApiClient(
    @Autowired val wikipediaWebClient: WebClient
) : WikipediaApiClient {
    override fun getBackground(wikipediaPage: String): Mono<WikipediaApi> {
        return wikipediaWebClient.get().uri(
            "/w/api.php/?action=query&prop=extracts&exsentences=10&exlimit=1&titles={wikipediaPage}&explaintext=1&formatversion=2&format=json",
            wikipediaPage
        )
            .retrieve()
            .bodyToMono(
                WikipediaSongDto::class.java
            )
            .onErrorResume { Mono.empty() }
            .switchIfEmpty(Mono.empty())
            .map {
                WikipediaApi(
                    background = "${formatText(it.query.pages.first().extract)}${createFooter(wikipediaPage)}"
                )
            }
    }

    private fun createFooter(wikipediaPage: String): String {
        return "\n\n[https://nl.wikipedia.org/wiki/$wikipediaPage](https://nl.wikipedia.org/wiki/$wikipediaPage)"
    }

    private fun formatText(wikiBackground: String): String {
        return wikiBackground
            .replace("\"", "*")
            .replace("..", ".")
            .replace("=== ", "###### ")
            .replace(" ===", " ")
            .replace("== ", "##### ")
            .replace(" ==", " ")
    }
}