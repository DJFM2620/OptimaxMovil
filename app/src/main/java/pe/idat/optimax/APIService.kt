package pe.idat.optimax

import okhttp3.ResponseBody
import org.json.JSONObject
import pe.idat.optimax.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET
    suspend fun getArticles(@Url url: String):Response<List<ArticleResponse>>

    @GET
    suspend fun getArticlesById(@Url url: String):Response<ArticleResponse>

    @GET
    suspend fun getDistrictById(@Url url: String):Response<DistrictResponse>

    @POST("Cliente/Agregar")
    suspend fun postNewClient(@Body clientDto: ClientDto): Response<*>

    @POST("InsertarVenta")
    suspend fun postnewVenta(@Body hashMap: HashMap<String,String>):Response<*>

    @GET
    suspend fun getClientByEmail(@Url url: String):Response<ClientDto>

    @POST("Recepcion")
    suspend fun postRecepcion(@Body hashMap: HashMap<String,String>):Response<*>

    @PUT("Cliente/Actualizar")
    suspend fun putUpdateClient(@Body clientDto: ClientDto):Response<*>

    @POST("Cita/Registrar")
    suspend fun postAppointment(@Body hashMap: HashMap<String,String>):Response<*>

    @POST("Token")
    suspend fun sendToken(@Body hashMap: HashMap<String, String>): Response<*>

    @PUT("Cliente/Actualizar")
    suspend fun putUpdateInfoClient(@Body clientInfoDto: ClientInfoDto):Response<*>

    @PUT("Cliente/Actualizar")
    suspend fun putUpdateAddInfoClient(@Body clientAddInfoDto: ClientAddInfoDto):Response<*>

    @GET
    suspend fun getOrdersByEmail(@Url url: String):Response<List<MyOrderResponse>>

    @GET
    suspend fun getAppointmetsByEmail(@Url url: String):Response<List<MyAppointmentResponse>>
}