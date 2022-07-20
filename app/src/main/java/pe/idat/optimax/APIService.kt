package pe.idat.optimax

import pe.idat.optimax.model.ArticleResponse
import pe.idat.optimax.model.ClientDto
import pe.idat.optimax.model.DistrictResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface APIService {

    @GET
    suspend fun getArticles(@Url url: String):Response<List<ArticleResponse>>

    @GET
    suspend fun getArticlesById(@Url url: String):Response<ArticleResponse>

    @GET
    suspend fun getDistrictById(@Url url: String):Response<DistrictResponse>

    @POST("Agregar")
    suspend fun postNewClient(@Body clientDto: ClientDto): Response<ClientDto>

    @POST("InsertarVenta")
    suspend fun postnewVenta(@Body hashMap: HashMap<String,String>):Response<*>

    @GET
    suspend fun getClientByEmail(@Url url: String):Response<Int>

    @POST("Recepcion")
    suspend fun postRecepcion(@Body hashMap: HashMap<String,String>):Response<*>
}