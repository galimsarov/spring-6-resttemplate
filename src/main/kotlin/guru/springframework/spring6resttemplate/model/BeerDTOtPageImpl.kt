package guru.springframework.spring6resttemplate.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@JsonIgnoreProperties(ignoreUnknown = true, value = ["pageable"])
class BeerDTOtPageImpl
@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
constructor(
    @JsonProperty("content") content: List<BeerDTO>,
    @JsonProperty("number") page: Int,
    @JsonProperty("size") size: Int,
    @JsonProperty("totalElements") total: Long
) : PageImpl<BeerDTO>(content, PageRequest.of(page, size), total)