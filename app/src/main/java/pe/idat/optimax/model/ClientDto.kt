package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class ClientDto(
                    @SerializedName("nombres")var name: String,
                    @SerializedName("apellidop")var pSurname: String,
                    @SerializedName("apellidom")var mSurname: String,
                    @SerializedName("dni")var DNI: Int,
                    @SerializedName("celular")var phone: Int,
                    @SerializedName("email")var email: String,
                    @SerializedName("ruc")var ruc: String = "",
                    @SerializedName("distrito")var codDistrict: DistrictResponse
                    )
