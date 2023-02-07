package pe.idat.optimax.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment:DialogFragment() {

    private var listener :TimePickerDialog.OnTimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = Calendar.HOUR
        val minute = Calendar.MINUTE

        return TimePickerDialog(requireActivity(),listener,hour,minute,true)
    }

    companion object{
        fun newInstance(listener: TimePickerDialog.OnTimeSetListener):TimePickerFragment {
            val fragment = TimePickerFragment()
            fragment.listener = listener

            return fragment
        }
    }
}