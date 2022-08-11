package pe.idat.optimax.culqui

import org.json.JSONObject

interface TokenCallBack {

    fun onSuccess(token: JSONObject?)

    fun onError(error: Exception?)
}