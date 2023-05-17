package guru.springframework.spring6resttemplate.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class BeerDTO(
    val id: UUID,
    val version: Int,
    val beerName: String,
    val beerStyle: BeerStyle,
    val upc: String,
    val quantityOnHand: Int,
    val price: BigDecimal,
    val createdDate: LocalDateTime,
    val updateDate: LocalDateTime
)
