package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerStyle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.web.client.HttpClientErrorException
import java.math.BigDecimal

@SpringBootTest
class BeerClientImplTest {
    @Autowired
    private lateinit var beerClient: BeerClient

    @Test
    fun listBeers() {
        beerClient.listBeers()
    }

    @Test
    fun listBeersWithName() {
        beerClient.listBeers(beerName = "ALE")
    }

    @Test
    fun listBeersWithStyle() {
        beerClient.listBeers(beerStyle = BeerStyle.ALE)
    }

    @Test
    fun listBeersWithNameAndWithoutInventory() {
        beerClient.listBeers(beerName = "ALE", showInventory = false)
    }

    @Test
    fun listBeersWithStyleAndPaging() {
        beerClient.listBeers(beerStyle = BeerStyle.ALE, pageNumber = 2, pageSize = 10)
    }

    @Test
    fun getBeerById() {
        val beerDTOs: Page<BeerDTO> = beerClient.listBeers()
        val dto: BeerDTO = beerDTOs.content[0]
        val byId: BeerDTO = beerClient.getBeerById(dto.id)
        assert(byId.beerName.isNotBlank())
    }

    @Test
    fun testCreateBeer() {
        val newDto = BeerDTO(
            price = BigDecimal(10.99),
            beerName = "Mango Bobs",
            beerStyle = BeerStyle.IPA,
            quantityOnHand = 500,
            upc = "123245"
        )
        val savedDto: BeerDTO = beerClient.createBeer(newDto)
        assert(savedDto.beerName.isNotBlank())
    }

    @Test
    fun testUpdateBeer() {
        val newDto = BeerDTO(
            price = BigDecimal(10.99),
            beerName = "Mango Bobs 2",
            beerStyle = BeerStyle.IPA,
            quantityOnHand = 500,
            upc = "123245"
        )
        val beerDto: BeerDTO = beerClient.createBeer(newDto)

        val newName = "Mango Bobs 3"
        beerDto.beerName = newName
        val updatedBeer: BeerDTO = beerClient.updateBeer(beerDto)

        assert(newName == updatedBeer.beerName)
    }

    @Test
    fun testDeleteBeer() {
        val newDto = BeerDTO(
            price = BigDecimal(10.99),
            beerName = "Mango Bobs 2",
            beerStyle = BeerStyle.IPA,
            quantityOnHand = 500,
            upc = "123245"
        )
        val beerDto: BeerDTO = beerClient.createBeer(newDto)

        beerClient.deleteBeer(beerDto.id)

        assertThrows<HttpClientErrorException> {
            beerClient.getBeerById(beerDto.id)
        }
    }
}