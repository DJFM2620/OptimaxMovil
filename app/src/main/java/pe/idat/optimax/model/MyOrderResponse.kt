package pe.idat.optimax.model

import com.google.gson.annotations.SerializedName

data class MyOrderResponse(
    @SerializedName("cod_pedido")var orderId: Int,
    @SerializedName("fecha")var date: String,
    @SerializedName("subTotal")var subTotal: Double,
    @SerializedName("estado")var state: String
)