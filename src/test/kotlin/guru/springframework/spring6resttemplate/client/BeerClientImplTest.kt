package guru.springframework.spring6resttemplate.client

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BeerClientImplTest {
    @Autowired
    private lateinit var beerClient: BeerClient

    @Test
    fun listBeersNoBeerName() {
        beerClient.listBeers()
    }

    @Test
    fun listBeers() {
        beerClient.listBeers("ALE")
    }
}