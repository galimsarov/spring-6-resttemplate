package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerDTOtPageImpl
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BeerClientImpl(private val restTemplateBuilder: RestTemplateBuilder) : BeerClient {
    private val getBeerPath = "/api/v1/beer"

    override fun listBeers(): Page<BeerDTO> {
        val restTemplate = restTemplateBuilder.build()

        val response: ResponseEntity<BeerDTOtPageImpl> =
            restTemplate.getForEntity(getBeerPath, BeerDTOtPageImpl::class.java)

        return response.body?.let { PageImpl(it.content) } ?: Page.empty()
    }
}