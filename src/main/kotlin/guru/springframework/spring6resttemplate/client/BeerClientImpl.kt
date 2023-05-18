package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerDTOtPageImpl
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BeerClientImpl(private val restTemplateBuilder: RestTemplateBuilder) : BeerClient {
    private val baseUrl = "http://localhost:8080"
    private val getBeerPath = "/api/v1/beer"

    override fun listBeers(): Page<BeerDTO> {
        val restTemplate = restTemplateBuilder.build()

        val pageResponse: ResponseEntity<BeerDTOtPageImpl> =
            restTemplate.getForEntity("$baseUrl$getBeerPath", BeerDTOtPageImpl::class.java)

        return Page.empty()
    }
}