package pe.idat.optimax.model

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pe.idat.optimax.culqui.Config
import pe.idat.optimax.culqui.TokenCallBack

class Token(s: String) {

    var config = Config()

    private val URL = "tokens/"

    private var api_key: String? = s

    private var listener: TokenCallBack? = null

    fun createToken(context: Context?, card: Card, listener: TokenCallBack) {
        this.listener = listener
        val requestQueue = Volley.newRequestQueue(context)
        var jsonBody = JSONObject()
        try {
            jsonBody = JSONObject()
            jsonBody.put("card_number", card.card_number)
            jsonBody.put("cvv", card.cvv)
            jsonBody.put("expiration_month", card.expiration_month)
            jsonBody.put("expiration_year", card.expiration_year)
            jsonBody.put("email", card.email)

        } catch (ex: Exception) {
            Log.v("", "ERROR: " + ex.message)
        }
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, config.url_base_secure + URL, jsonBody,
                Response.Listener { response ->
                    try {
                        listener.onSuccess(response)
                    } catch (ex: Exception) {
                        listener.onError(ex)
                    }
                },
                Response.ErrorListener { error -> listener.onError(error) }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> = HashMap()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["Authorization"] = "Bearer $api_key"
                    return headers
                }
            }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }
}