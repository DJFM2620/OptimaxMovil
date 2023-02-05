package pe.idat.optimax.fragments

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import pe.idat.optimax.APIService
import pe.idat.optimax.NullOnEmptyConverterFactory
import pe.idat.optimax.R
import pe.idat.optimax.culqui.TokenCallBack
import pe.idat.optimax.culqui.Validation
import pe.idat.optimax.databinding.FragmentPaymentBinding
import pe.idat.optimax.model.Card
import pe.idat.optimax.model.Token
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PaymentFragment : Fragment() {

    private lateinit var mBinding: FragmentPaymentBinding

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"

    var validation: Validation? = null
    var progress: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentPaymentBinding.inflate(inflater,container,false)

        validation = Validation()

        progress = ProgressDialog(activity)
        progress!!.setMessage("Validando informacion de la tarjeta")
        progress!!.setCancelable(false)
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        var orderMap: HashMap<String, String> = arguments?.getSerializable("order") as HashMap<String, String>

        mBinding.tvTotal.text = orderMap["totaltv"].toString()
        mBinding.tvDirection.text =  "Direccion de envio: ${orderMap["direction"]}"

        mBinding.ietCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    mBinding.ietCVV.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable) {
                val text = mBinding.ietCardNumber.text.toString()
                if (s.isEmpty()) {
                    //mBinding.ietCardNumber.setBackgroundResource(R.drawable.border_error)
                    mBinding.tilCardNumber.boxStrokeColor = Color.RED
                }
                if (validation!!.luhn(text)) {
                    //mBinding.ietCardNumber.setBackgroundResource(R.drawable.border_sucess)
                    mBinding.tilCardNumber.boxStrokeColor = Color.GREEN
                } else {
                    //mBinding.ietCardNumber.setBackgroundResource(R.drawable.border_error)
                    mBinding.tilCardNumber.boxStrokeColor = Color.RED
                }
                val cvv: Int = validation!!.bin(text, mBinding.kindCard)
                if (cvv > 0) {
                    mBinding.ietCVV.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(cvv))
                    mBinding.ietCVV.isEnabled = true
                } else {
                    mBinding.ietCVV.isEnabled = false
                    mBinding.ietCVV.setText("")
                }
            }
        })

        mBinding.ietYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text: String = mBinding.ietYear.text.toString()
                if (validation!!.year(text)) {
                    //mBinding.ietYear.setBackgroundResource(R.drawable.border_error)
                    mBinding.tilYear.boxStrokeColor = Color.RED
                } else {
                    //mBinding.ietYear.setBackgroundResource(R.drawable.border_sucess)
                    mBinding.tilYear.boxStrokeColor = Color.GREEN
                }
            }
        })

        mBinding.ietMonth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text: String = mBinding.ietMonth.text.toString()
                if (validation!!.month(text)) {
                    //mBinding.ietMonth.setBackgroundResource(R.drawable.border_error)
                    mBinding.tilMonth.boxStrokeColor = Color.RED
                } else {
                    //mBinding.ietMonth.setBackgroundResource(R.drawable.border_sucess)
                    mBinding.tilMonth.boxStrokeColor = Color.GREEN
                }
            }
        })

        mBinding.btnPay.setOnClickListener {
            progress!!.show()

            val card = Card(mBinding.ietCardNumber.text.toString(),
                mBinding.ietCVV.text.toString(),
                mBinding.ietMonth.text.toString(),
                mBinding.ietYear.text.toString(),
                mBinding.ietEmail.text.toString())

            val token = Token("pk_test_c45c56aa6dc7d27c")

            token.createToken(context, card, object : TokenCallBack {
                override fun onSuccess(token: JSONObject) {
                    try {
                        var orderMap: HashMap<String, String> = arguments?.getSerializable("order") as HashMap<String, String>

                        val map= HashMap<String,String>()
                        map["key"] = token["id"].toString()
                        map["total"] = orderMap["subtotal"].toString()
                        map["email"] = orderMap["email_client"].toString()

                        CoroutineScope(Dispatchers.IO).launch {
                            val call = getRetrofit().create(APIService::class.java).sendToken(map)
                            if(call.isSuccessful){

                                val callOrder =getRetrofit().create(APIService::class.java).postnewVenta(orderMap)
                                if (callOrder.isSuccessful){
                                    mBinding.messageConfirm.text = "La transaccion fue exitosa...!"
                                }
                            }else{
                                mBinding.messageConfirm.text = "Hubo un problema con la transaccion...!"
                            }
                        }
                    } catch (ex: Exception) {
                        progress!!.hide()
                    }
                    progress!!.hide()
                }
                override fun onError(error: Exception) {
                    progress!!.hide()
                }
            })
        }
        return mBinding.root
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}