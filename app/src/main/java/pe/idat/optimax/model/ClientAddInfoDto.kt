package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class ClientAddInfoDto (
    @SerializedName("celular")var phone: Int = 0,
    @SerializedName("email")var email: String,
    @SerializedName("direccion")var direction: String = "",
    @SerializedName("dni")var DNI: Int = 0,
    @SerializedName("distrito")var district: DistrictDto
    )