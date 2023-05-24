package guru.springframework.spring6resttemplate.client

import com.fasterxml.jackson.databind.ObjectMapper
import guru.springframework.spring6resttemplate.config.OAuthClientInterceptor
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig
import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerDTOtPageImpl
import guru.springframework.spring6resttemplate.model.BeerStyle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.math.BigDecimal
import java.net.URI
import java.time.Instant
import java.util.*

@Import(RestTemplateBuilderConfig::class)
@RestClientTest
class BeerClientMockTest {
    private val url = "http://localhost:8080"
    private val bearerTest = "Bearer test"

    @Autowired
    private lateinit var restTemplateBuilderConfigured: RestTemplateBuilder

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Mock
    val mockRestTemplateBuilder: RestTemplateBuilder = RestTemplateBuilder(MockServerRestTemplateCustomizer())

    @TestConfiguration
    class TestConfig {
        @Bean
        fun clientRegistrationRepository(): ClientRegistrationRepository {
            return InMemoryClientRegistrationRepository(
                ClientRegistration
                    .withRegistrationId("springauth")
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .clientId("test")
                    .tokenUri("test")
                    .build()
            )
        }

        @Bean
        fun auth2AuthorizedClientService(
            clientRegistrationRepository: ClientRegistrationRepository
        ): OAuth2AuthorizedClientService {
            return InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
        }

        @Bean
        fun oAuthClientInterceptor(
            manager: OAuth2AuthorizedClientManager, clientRegistrationRepository: ClientRegistrationRepository
        ): OAuthClientInterceptor {
            return OAuthClientInterceptor(manager, clientRegistrationRepository)
        }
    }

    @MockBean
    private lateinit var manager: OAuth2AuthorizedClientManager

    private lateinit var beerClient: BeerClient

    private lateinit var server: MockRestServiceServer

    private lateinit var dto: BeerDTO
    private lateinit var dtoJson: String

    @BeforeEach
    fun setUp() {
        val clientRegistration: ClientRegistration = clientRegistrationRepository.findByRegistrationId("springauth")

        val token = OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test", Instant.MIN, Instant.MAX)

        Mockito
            .`when`(manager.authorize(any()))
            .thenReturn(OAuth2AuthorizedClient(clientRegistration, "test", token))

        val restTemplate: RestTemplate = restTemplateBuilderConfigured.build()
        server = MockRestServiceServer.bindTo(restTemplate).build()
        Mockito.`when`(mockRestTemplateBuilder.build()).thenReturn(restTemplate)
        beerClient = BeerClientImpl(mockRestTemplateBuilder)

        dto = getBeerDto()
        dtoJson = objectMapper.writeValueAsString(dto)
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
        mockGetOperation()

        val responseDto: BeerDTO = beerClient.getBeerById(dto.id)
        assertThat(responseDto.id).isEqualTo(dto.id)
    }

    @Test
    fun testCreateBeer() {
        val uri: URI = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH).build(dto.id)

        server.expect(method(HttpMethod.POST))
            .andExpect(requestTo("$url${BeerClientImpl.GET_BEER_PATH}"))
            .andRespond(withAccepted().location(uri))

        mockGetOperation()

        val responseDto: BeerDTO = beerClient.createBeer(dto)
        assertThat(responseDto.id).isEqualTo(dto.id)
    }

    @Test
    fun testUpdateBeer() {
        server.expect(method(HttpMethod.PUT))
            .andExpect(requestToUriTemplate("$url${BeerClientImpl.GET_BEER_BY_ID_PATH}", dto.id))
            .andRespond(withNoContent())

        mockGetOperation()

        val responseDto: BeerDTO = beerClient.updateBeer(dto)
        assertThat(responseDto.id).isEqualTo(dto.id)
    }

    @Test
    fun testDeleteBeer() {
        server.expect(method(HttpMethod.DELETE))
            .andExpect(requestToUriTemplate("$url${BeerClientImpl.GET_BEER_BY_ID_PATH}", dto.id))
            .andRespond(withNoContent())

        beerClient.deleteBeer(dto.id)

        server.verify()
    }

    @Test
    fun testDeleteNotFound() {
        server.expect(method(HttpMethod.DELETE))
            .andExpect(requestToUriTemplate("$url${BeerClientImpl.GET_BEER_BY_ID_PATH}", dto.id))
            .andRespond(withResourceNotFound())

        assertThrows<HttpClientErrorException> { beerClient.deleteBeer(dto.id) }

        server.verify()
    }

    @Test
    fun testListBeersWithQueryParam() {
        val response: String = objectMapper.writeValueAsString(getPage())

        val uri: URI = UriComponentsBuilder
            .fromHttpUrl("$url${BeerClientImpl.GET_BEER_PATH}")
            .queryParam("beerName", "ALE")
            .queryParam("beerStyle", "")
            .queryParam("showInventory", "true")
            .queryParam("pageNumber", "1")
            .queryParam("pageSize", "25")
            .build().toUri()

        server.expect(method(HttpMethod.GET))
            .andExpect(requestTo(uri))
            .andExpect(header("Authorization", bearerTest))
            .andExpect(queryParam("beerName", "ALE"))
            .andExpect(queryParam("beerStyle", ""))
            .andExpect(queryParam("showInventory", "true"))
            .andExpect(queryParam("pageNumber", "1"))
            .andExpect(queryParam("pageSize", "25"))
            .andRespond(withSuccess(response, MediaType.APPLICATION_JSON))

        val responsePage: Page<BeerDTO> = beerClient.listBeers(beerName = "ALE", showInventory = true)

        assertThat(responsePage.content.size).isGreaterThan(0)
    }

    private fun mockGetOperation() {
        server.expect(method(HttpMethod.GET))
            .andExpect(requestToUriTemplate("$url${BeerClientImpl.GET_BEER_BY_ID_PATH}", dto.id))
            .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON))
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