package pe.idat.optimax.model

data class ArticleCartDto(var codArticle: String,
                          var price: String,
                          var imagen: String,
                          var quantity: String = "")
