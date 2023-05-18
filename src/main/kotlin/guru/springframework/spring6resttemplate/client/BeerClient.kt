package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerDTO
import guru.springframework.spring6resttemplate.model.BeerStyle
import org.springframework.data.domain.Page
import java.util.*

interface BeerClient {
    fun listBeers(
        beerName: String = "",
        beerStyle: BeerStyle = BeerStyle.NONE,
        showInventory: Boolean? = null,
        pageNumber: Int = 1,
        pageSize: Int = 25
    ): Page<BeerDTO>

    fun getBeerById(beerId: UUID): BeerDTO
    fun createBeer(newDto: BeerDTO): BeerDTO
    fun updateBeer(beerDto: BeerDTO): BeerDTO
    fun deleteBeer(beerId: UUID)
}