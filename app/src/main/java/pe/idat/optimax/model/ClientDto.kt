package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class ClientDto(
                    @SerializedName("cod_Cliente")var cod_client: Int,
                    @SerializedName("nombres")var name: String,
                    @SerializedName("apellidop")var pSurname: String,
                    @SerializedName("apellidom")var mSurname: String,
                    @SerializedName("dni")var DNI: Int = 0,
                    @SerializedName("celular")var phone: Int = 0,
                    @SerializedName("email")var email: String,
                    @SerializedName("ruc")var ruc: String = "",
                    @SerializedName("direccion")var direction: String = "",
                    @SerializedName("distrito")var district: DistrictDto
                    )
