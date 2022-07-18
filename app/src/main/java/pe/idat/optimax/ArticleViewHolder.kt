package pe.idat.optimax

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import pe.idat.optimax.model.ArticleResponse
import pe.idat.optimax.databinding.ItemArticleBinding

class ArticleViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemArticleBinding.bind(view)

    fun bind(article: ArticleResponse){

        Glide.with(this.binding.root.context)
             .load(convertBase64ToBitmap(article.imagen))
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .centerCrop()
             .into(binding.ivImage)

        binding.tvPrice.text = article.price
    }

    private fun convertBase64ToBitmap(b64: String): Bitmap? {

        val imageAsBytes = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }
}