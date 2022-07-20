package pe.idat.optimax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArticleResponse(@SerializedName("codArticulo") var codArticle: String,
                           @SerializedName("precio") var price: String,
                           @SerializedName("imagen") var imagen: String) : Serializable{

}
