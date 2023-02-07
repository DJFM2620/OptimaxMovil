package pe.idat.optimax

import pe.idat.optimax.model.*
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET
    suspend fun getArticles(@Url url: String):Response<List<ArticleResponse>>

    @GET
    suspend fun getArticlesById(@Url url: String):Response<ArticleResponse>

    @POST("Cliente/Agregar")
    suspend fun postNewClient(@Body clientDto: ClientDto): Response<*>

    @GET
    suspend fun getDistricts(@Url url: String): Response<List<DistrictResponse>>

    @GET
    suspend fun getDirectionClient(@Url url: String): Response<String>

    @GET
    suspend fun getValidateClientByDni(@Url url: String):Response<Int>

    @GET
    suspend fun getClientByEmail(@Url url: String):Response<ClientDto>

    @POST("InsertarVenta")
    suspend fun postnewVenta(@Body hashMap: HashMap<String,String>):Response<*>

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