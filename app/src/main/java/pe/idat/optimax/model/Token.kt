package pe.idat.optimax.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import pe.idat.optimax.APIService
import pe.idat.optimax.NullOnEmptyConverterFactory
import pe.idat.optimax.culqui.Config
import pe.idat.optimax.culqui.TokenCallBack
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class Token(s: String) {

    var config = Config()

    private var api_key: String? = null

    private var listener: TokenCallBack? = null

    private fun getGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    private fun getRetrofit(): Retrofit {

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request()
                                        .newBuilder()
                                        .addHeader("Content-type", "application/json")
                                        .addHeader("Authorization", "Bearer pk_test_c45c56aa6dc7d27c")
                                        .build()
            chain.proceed(request)
        }

        return Retrofit.Builder()
            .baseUrl(config.url_base_secure)
            .client(httpClient.build())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            /*.client(getClient())*/
            .build()
    }

    fun createToken(context: Context?, card: Card, listener: TokenCallBack) {
        this.listener = listener

        var jsonBody = JSONObject()
        try {
            jsonBody = JSONObject()
            jsonBody.put("card_number", card.card_number)
            jsonBody.put("cvv", card.cvv)
            jsonBody.put("expiration_month", card.expiration_month)
            jsonBody.put("expiration_year", card.expiration_year)
            jsonBody.put("email", card.email)

        } catch (ex: java.lang.Exception) {
            Log.v("", "ERROR: " + ex.message)
        }
    }
}