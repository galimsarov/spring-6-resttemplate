package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerStyle
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
}