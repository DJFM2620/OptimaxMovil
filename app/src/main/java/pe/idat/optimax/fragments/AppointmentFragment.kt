package pe.idat.optimax.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.APIService
import pe.idat.optimax.NullOnEmptyConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pe.idat.optimax.databinding.FragmentAppointmentBinding
import pe.idat.optimax.model.ArticleResponse
import java.util.HashMap

class AppointmentFragment : Fragment() {

    private lateinit var mBinding: FragmentAppointmentBinding
    private val baseURL: String = "http://192.168.1.41:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentAppointmentBinding.inflate(inflater,container,false)

        mBinding.etPlannedDate.setOnClickListener {
            showDatePickerDialog()
        }
        mBinding.etPlannedhour.setOnClickListener {
            showTimePickerDialog()
        }

        mBinding.btnRegisterAppointment.setOnClickListener {
            insertAppointment()
        }

        return mBinding.root
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            /*.client(getClient())*/
            .build()
    }

    private fun insertAppointment(){

        val user = Firebase.auth.currentUser
        val email = user!!.email.toString()

        val map= HashMap<String,String>()
        map["email"] = email
        map["date"] = mBinding.etPlannedDate.text.toString()
        map["hour"] = mBinding.etPlannedhour.text.toString()

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).postAppointment(map)

            activity?.runOnUiThread {
                if (call.isSuccessful) {

                    Toast.makeText( activity, "Cita registrada exitosamente...!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText( activity, "Error al registrar la cita", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePickerDialog(){
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener{ _, year, month, day ->
            // +1 because January is zero
            //val selectedDate = day.toString() + "-" + (month + 1) + "-" + year
            //mBinding.etPlannedDate.setText(selectedDate)

            var y : String = year.toString()
            var m = "${month + 1}"
            var d : String = day.toString()

            if(month < 10){

                m = "0"+(month+1)
                Log.d("MENSAJE","SE VALIDO EL IF DEL MES")
            }
            if(day < 10){

                d = "0$day"
                Log.d("MENSAJE","SE VALIDO EL IF DEL DIA")
            }

            mBinding.etPlannedDate.setText("$y-$m-$d")
        })
        /* VALIDAR LOS 0 AL COMIENZO*/
        newFragment.show(requireFragmentManager(), "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragmentTime =
            TimePickerFragment.newInstance(TimePickerDialog.OnTimeSetListener { _, hour, minute ->

                var h = hour.toString()
                var m = minute.toString()

                if(hour < 10){

                    h = "0$hour"
                }
                if(minute < 10 ){

                    m = "0$minute"
                }
                mBinding.etPlannedhour.setText("$h:$m")
            })
        newFragmentTime.show(requireFragmentManager(), "timepicker")
    }
}