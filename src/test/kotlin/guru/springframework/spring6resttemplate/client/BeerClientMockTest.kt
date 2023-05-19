package guru.springframework.spring6resttemplate.client

import com.fasterxml.jackson.databind.ObjectMapper
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig
import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerDTOtPageImpl
import guru.springframework.spring6resttemplate.model.BeerStyle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.util.*

@Import(RestTemplateBuilderConfig::class)
@RestClientTest
class BeerClientMockTest {
    private val url = "http://localhost:8080"

    @Autowired
    private lateinit var restTemplateBuilderConfigured: RestTemplateBuilder

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Mock
    val mockRestTemplateBuilder: RestTemplateBuilder = RestTemplateBuilder(MockServerRestTemplateCustomizer())

    private lateinit var beerClient: BeerClient

    private lateinit var server: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        val restTemplate: RestTemplate = restTemplateBuilderConfigured.build()
        server = MockRestServiceServer.bindTo(restTemplate).build()
        Mockito.`when`(mockRestTemplateBuilder.build()).thenReturn(restTemplate)
        beerClient = BeerClientImpl(mockRestTemplateBuilder)
    }

    @Test
    fun testListBeers() {
        val payload: String = objectMapper.writeValueAsString(getPage())

        server.expect(method(HttpMethod.GET))
            .andExpect(requestTo("$url${BeerClientImpl.GET_BEER_PATH}${BeerClientImpl.DEFAULT_PARAMS}"))
            .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON))

        val dtoPage: Page<BeerDTO> = beerClient.listBeers()
        assertThat(dtoPage.content.size).isGreaterThan(0)
    }

    @Test
    fun testGetBeerById() {
        val dto = getBeerDto()
        val response: String = objectMapper.writeValueAsString(dto)

        server.expect(method(HttpMethod.GET))
            .andExpect(requestToUriTemplate("$url${BeerClientImpl.GET_BEER_BY_ID_PATH}", dto.id))
            .andRespond(withSuccess(response, MediaType.APPLICATION_JSON))

        val responseDto: BeerDTO = beerClient.getBeerById(dto.id)
        assertThat(responseDto.id).isEqualTo(dto.id)
    }

    private fun getBeerDto() = BeerDTO(
        price = BigDecimal(10.99),
        beerName = "Mango Bobs",
        beerStyle = BeerStyle.IPA,
        quantityOnHand = 500,
        upc = "123245"
    )

    private fun getPage() = BeerDTOtPageImpl(listOf(getBeerDto()), 1, 25, 1)
}