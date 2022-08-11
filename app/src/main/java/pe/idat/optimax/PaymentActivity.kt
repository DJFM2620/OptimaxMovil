package pe.idat.optimax

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import pe.idat.optimax.model.Card
import pe.idat.optimax.model.Token
import pe.idat.optimax.culqui.Validation
import org.json.JSONObject
import pe.idat.optimax.culqui.TokenCallBack
import pe.idat.optimax.databinding.ActivityLoginBinding
import pe.idat.optimax.databinding.ActivityPaymentGateawayBinding


class PaymentActivity: AppCompatActivity() {

    var validation: Validation? = null

    var progress: ProgressDialog? = null

    private lateinit var txtcardnumber: TextView
    private lateinit var txtcvv:TextView
    private lateinit var txtmonth:TextView
    private lateinit var txtyear:TextView
    private lateinit var txtemail:TextView
    private lateinit var kind_card:TextView
    private lateinit var result:TextView
    private lateinit var btnPay: Button

    private lateinit var mBinding: ActivityPaymentGateawayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPaymentGateawayBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        validation = Validation()
        progress = ProgressDialog(this)
        progress!!.setMessage("Validando informacion de la tarjeta")
        progress!!.setCancelable(false)
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        txtcardnumber = findViewById<View>(R.id.txtCardNumber) as TextView
        txtcvv = findViewById<View>(R.id.txt_cvv) as TextView
        txtmonth = findViewById<View>(R.id.txt_month) as TextView
        txtyear = findViewById<View>(R.id.txt_year) as TextView
        txtemail = findViewById<View>(R.id.txt_email) as TextView
        kind_card = findViewById<View>(R.id.kind_card) as TextView
        result = findViewById<View>(R.id.token_id) as TextView
        btnPay = findViewById<View>(R.id.btn_pay) as Button
        txtcvv!!.isEnabled = false

        txtcardnumber!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    txtcvv!!.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable) {
                val text = txtcardnumber!!.text.toString()
                if (s.isEmpty()) {
                    txtcardnumber!!.setBackgroundResource(R.drawable.border_error)
                }
                if (validation!!.luhn(text)) {
                    txtcardnumber!!.setBackgroundResource(R.drawable.border_sucess)
                } else {
                    txtcardnumber!!.setBackgroundResource(R.drawable.border_error)
                }
                val cvv: Int = validation!!.bin(text, kind_card!!)
                if (cvv > 0) {
                    txtcvv!!.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(cvv)))
                    txtcvv!!.setEnabled(true)
                } else {
                    txtcvv!!.setEnabled(false)
                    txtcvv!!.setText("")
                }
            }
        })

        txtyear!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text: String = txtyear!!.getText().toString()
                if (validation!!.year(text)) {
                    txtyear!!.setBackgroundResource(R.drawable.border_error)
                } else {
                    txtyear!!.setBackgroundResource(R.drawable.border_sucess)
                }
            }
        })

        txtmonth!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text: String = txtmonth!!.getText().toString()
                if (validation!!.month(text)) {
                    txtmonth!!.setBackgroundResource(R.drawable.border_error)
                } else {
                    txtmonth!!.setBackgroundResource(R.drawable.border_sucess)
                }
            }
        })

        btnPay.setOnClickListener {

            progress?.show()
            val card = Card(card_number = mBinding.txtCardNumber.text.trim().toString(),
                            cvv = mBinding.txtCvv.text.trim().toString(),
                            expiration_month = mBinding.txtMonth.text.trim().toString(),
                            expiration_year = mBinding.txtYear.text.trim().toString(),
                            email = mBinding.txtEmail.text.trim().toString())

            val token = Token("Bearer pk_test_c45c56aa6dc7d27c")


        }
    }

}