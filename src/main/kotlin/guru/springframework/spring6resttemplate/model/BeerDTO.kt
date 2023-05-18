package guru.springframework.spring6resttemplate.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class BeerDTO(
    val id: UUID = UUID.randomUUID(),
    val version: Int = 0,
    var beerName: String = "",
    val beerStyle: BeerStyle = BeerStyle.NONE,
    val upc: String = "",
    val quantityOnHand: Int = 0,
    val price: BigDecimal = BigDecimal(0),
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val updateDate: LocalDateTime = LocalDateTime.now()
)
