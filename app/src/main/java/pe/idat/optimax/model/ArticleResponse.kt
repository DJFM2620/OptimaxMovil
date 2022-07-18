package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class ArticleResponse(@SerializedName("codArticulo") var codArticle: String,
                           @SerializedName("precio") var price: String,
                           @SerializedName("imagen") var imagen: String) {
}