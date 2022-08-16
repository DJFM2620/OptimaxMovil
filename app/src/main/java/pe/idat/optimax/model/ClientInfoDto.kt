package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class ClientInfoDto (
    @SerializedName("nombres")var name: String,
    @SerializedName("email")var email: String,
    @SerializedName("apellidop")var pSurname: String,
    @SerializedName("apellidom")var mSurname: String,
    @SerializedName("dni")var DNI: Int
)