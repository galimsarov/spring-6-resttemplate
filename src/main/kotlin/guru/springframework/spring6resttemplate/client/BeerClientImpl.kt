package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerDTOtPageImpl
import guru.springframework.spring6resttemplate.model.BeerStyle
import guru.springframework.spring6resttemplate.model.toStringParam
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class BeerClientImpl(private val restTemplateBuilder: RestTemplateBuilder) : BeerClient {
    private val getBeerPath = "/api/v1/beer"

    override fun listBeers(
        beerName: String,
        beerStyle: BeerStyle,
        showInventory: Boolean?,
        pageNumber: Int,
        pageSize: Int
    ): Page<BeerDTO> {
        val restTemplate = restTemplateBuilder.build()

        val uriComponentsBuilder: UriComponentsBuilder = UriComponentsBuilder.fromPath(getBeerPath)

        uriComponentsBuilder
            .queryParam("beerName", beerName)
            .queryParam("beerStyle", beerStyle.toStringParam())
            .queryParam("showInventory", showInventory)
            .queryParam("pageNumber", pageNumber)
            .queryParam("pageSize", pageSize)

        val response: ResponseEntity<BeerDTOtPageImpl> =
            restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOtPageImpl::class.java)

        return response.body?.let { PageImpl(it.content) } ?: Page.empty()
    }
}