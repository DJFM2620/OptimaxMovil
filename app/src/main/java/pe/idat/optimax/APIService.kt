package pe.idat.optimax

import retrofit2.Call
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

    @POST
    suspend fun postNewClient(@Url url: String, @Body clientDto: ClientDto): Call<ClientDto>
}