package guru.springframework.spring6resttemplate.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
class RestTemplateBuilderConfig {
    @Value("\${rest.template.rootUrl}")
    private lateinit var rootUrl: String

    @Value("\${rest.template.username}")
    private lateinit var userName: String

    @Value("\${rest.template.password}")
    private lateinit var password: String

    @Bean
    fun restTemplateBuilder(configurer: RestTemplateBuilderConfigurer): RestTemplateBuilder {
        assert(rootUrl.isNotBlank())

        return configurer.configure(RestTemplateBuilder()).basicAuthentication(userName, password)
            .uriTemplateHandler(DefaultUriBuilderFactory(rootUrl))
    }
}