package guru.springframework.spring6resttemplate.client

import guru.springframework.spring6resttemplate.model.BeerDTO
import org.springframework.data.domain.Page

interface BeerClient {
    fun listBeers(): Page<BeerDTO>
}