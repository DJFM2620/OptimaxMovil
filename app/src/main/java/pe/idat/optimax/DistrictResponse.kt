package pe.idat.optimax

import com.google.gson.annotations.SerializedName

data class DistrictResponse(@SerializedName("cod_distrito") var codDistrict: Int,
                            @SerializedName("nombredistr") var name: String)
