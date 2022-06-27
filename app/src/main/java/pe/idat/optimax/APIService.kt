package pe.idat.optimax

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {

    @GET
    fun getArticles(@Url url: String):Response<ArticleResponse>

    @GET
    suspend fun getArticlesById(@Url url: String):Response<ArticleResponse>
}