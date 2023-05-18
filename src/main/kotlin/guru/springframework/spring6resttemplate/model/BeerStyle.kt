package guru.springframework.spring6resttemplate.model

enum class BeerStyle { LAGER, PILSNER, STOUT, GOSE, PORTER, ALE, WHEAT, IPA, PALE_ALE, SAISON, NONE }

fun BeerStyle.toStringParam(): String = if (this == BeerStyle.NONE) "" else toString()