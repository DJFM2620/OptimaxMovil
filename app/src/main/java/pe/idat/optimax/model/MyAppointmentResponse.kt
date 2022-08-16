package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class MyAppointmentResponse(
    @SerializedName("cod_Cita")var appointmentId: Int,
    @SerializedName("fecha")var date: String,
    @SerializedName("hora")var hour: String
)
