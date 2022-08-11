package pe.idat.optimax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.jetbrains.anko.doAsync
import pe.idat.optimax.databinding.ItemArticleBinding
import pe.idat.optimax.databinding.ItemArticleCartBinding
import pe.idat.optimax.model.ArticleEntity
import pe.idat.optimax.model.ArticleResponse

class ArticleAdapter(private val articles: MutableList<ArticleResponse>):RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view){

        val binding = ItemArticleBinding.bind(view)

        fun setListener(articleResponse: ArticleResponse){

            binding.addItemCart.setOnClickListener {

                doAsync {
                    val articleEntity = ArticleEntity(
                        articleId = articleResponse.codArticle.toInt(),
                        price = articleResponse.price,
                        quantity = 1,
                        image = articleResponse.imagen
                    )

                    OptimaxApplication.database.OptimaxDao().insertDB(articleEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = articles[position]

        holder.setListener(item)

        Glide.with(mContext)
            .load(convertBase64ToBitmap(item.imagen))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.binding.ivImage)

        holder.binding.tvPrice.text = item.price
    }

    override fun getItemCount(): Int {

        return articles.size
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {

        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }
}