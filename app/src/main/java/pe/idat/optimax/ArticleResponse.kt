package pe.idat.optimax

import com.google.gson.annotations.SerializedName

data class ArticleResponse(@SerializedName("codArticulo") var codArticle: String,
                           @SerializedName("precio") var price: String,
                           @SerializedName("imagen") var imagen: String) {
}