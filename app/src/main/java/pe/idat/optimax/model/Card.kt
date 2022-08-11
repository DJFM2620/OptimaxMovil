package pe.idat.optimax.model

data class Card(
    var card_number: String,
    var cvv: String,
    var expiration_month: String,
    var expiration_year: String,
    var email: String,
)
