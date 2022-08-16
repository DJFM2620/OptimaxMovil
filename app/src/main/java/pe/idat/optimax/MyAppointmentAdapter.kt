package pe.idat.optimax

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.idat.optimax.databinding.ItemAppointmentBinding
import pe.idat.optimax.databinding.ItemOrdersBinding
import pe.idat.optimax.model.MyAppointmentResponse
import pe.idat.optimax.model.MyOrderResponse

class MyAppointmentAdapter (private val appointments: MutableList<MyAppointmentResponse>): RecyclerView.Adapter<MyAppointmentAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val binding = ItemAppointmentBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAppointmentAdapter.ViewHolder {

        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appointments[position]

        holder.binding.tvDate.text = item.date
        holder.binding.tvAppointmentId.text = item.appointmentId.toString()
        holder.binding.tvHour.text = item.hour
    }

    override fun getItemCount(): Int {

        return appointments.size
    }
}