package pe.idat.optimax

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.jetbrains.anko.doAsync
import pe.idat.optimax.databinding.ItemOrdersBinding
import pe.idat.optimax.model.ArticleEntity
import pe.idat.optimax.model.ArticleResponse
import pe.idat.optimax.model.MyOrderResponse

class MyOrderAdapter(private val orders: MutableList<MyOrderResponse>): RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val binding = ItemOrdersBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderAdapter.ViewHolder {

        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_orders, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = orders[position]

        holder.binding.tvDate.text = item.date
        holder.binding.tvOrderId.text = item.orderId.toString()
        holder.binding.tvTotal.text = item.subTotal.toString()
        holder.binding.tvState.text = item.state
    }

    override fun getItemCount(): Int {

        return orders.size
    }
}