package pe.idat.optimax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pe.idat.optimax.databinding.ItemArticleCartBinding
import pe.idat.optimax.model.ArticleEntity
import java.util.HashMap

class CartArticleAdapter(private var articles: MutableList<ArticleEntity>,
                         private var listener: OnClickListener):RecyclerView.Adapter<CartArticleAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    val quantities= HashMap<String,Int>()

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view){

        val binding = ItemArticleCartBinding.bind(view)

        fun deleteCart(articleEntity: ArticleEntity) {

            binding.deletecart.setOnClickListener {

                doAsync {
                    OptimaxApplication.database.OptimaxDao().deleteDB(articleEntity)

                    uiThread {
                        val index = articles.indexOf(articleEntity)

                        if (index != -1) {
                            articles.removeAt(index)
                            notifyItemRemoved(index) //refrescar los cambios

                            var sum = 0.0

                            for ( i in 0 until articles.size){

                                sum += (articles[i].price.toDouble() * quantities["${articles[i].articleId}"]!!.toInt())
                            }
                            listener.setTotal(sum)
                            quantities.remove("${articleEntity.articleId}")
                        }
                    }
                }
            }
        }

        fun getTotal(articleEntity: ArticleEntity){

            binding.btnAddQuantity.setOnClickListener {

                var quantity = binding.tvQuantity.text.trim().toString()
                var quantityInc = (quantity.toInt().inc()).toString()

                binding.tvQuantity.text = quantityInc
                binding.tvTotal.text = (quantityInc.toInt() * articleEntity.price.toDouble()).toString()

                listener.sumPrice(binding.tvPrice.text.toString().toDouble())
                quantities["${articleEntity.articleId}"] = binding.tvQuantity.text.toString().toInt()
            }

            binding.btnRemoveQuantity.setOnClickListener{

                if(binding.tvQuantity.text.toString().toInt() == 1){
                    binding.tvQuantity.text = "1"
                }
                else if(binding.tvQuantity.text.toString().toInt() > 0){

                    var quantity = binding.tvQuantity.text.trim().toString()
                    var quantityDec = (quantity.toInt().dec()).toString()

                    binding.tvQuantity.text = quantityDec
                    binding.tvTotal.text = (quantityDec.toInt() * articleEntity.price.toDouble()).toString()

                    listener.subsTractPrice(binding.tvPrice.text.toString().toDouble())

                    quantities["${articleEntity.articleId}"] = binding.tvQuantity.text.toString().toInt()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_article_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = articles[position]

        holder.getTotal(item)

        var sum = 0.0

        for ( i in 0 until articles.size){

            sum += articles[i].price.toDouble()

        }
        quantities["${item.articleId}"] = item.quantity

        listener.setTotal(sum)

        Glide.with(mContext)
            .load(convertBase64ToBitmap(item.image))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.binding.ivImage)

        holder.binding.tvCod.text = item.articleId.toString()
        holder.binding.tvPrice.text = item.price
        holder.binding.tvTotal.text = item.price

        holder.deleteCart(item)
    }

    override fun getItemCount(): Int {

        return articles.size
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {

        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    fun setCollection(optimaxDB: MutableList<ArticleEntity>) {
        this.articles = optimaxDB
        notifyDataSetChanged() //refrescar los cambios
    }
}