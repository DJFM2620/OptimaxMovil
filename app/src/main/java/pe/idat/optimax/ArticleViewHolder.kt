package pe.idat.optimax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso
import pe.idat.optimax.databinding.ItemArticleBinding


class ArticleViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemArticleBinding.bind(view)

    fun bind(article:ArticleResponse){

        //Picasso.get().load(convertBase64ToBitmap(article.imagen)).into(binding.ivImage)

        //binding.ivImage.setImageBitmap(convertBase64ToBitmap(article.imagen))
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

    fun transform(source: Bitmap): Bitmap? {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val result = Bitmap.createBitmap(source, x, y, size, size)
        if (result != source) {
            source.recycle()
        }
        return result
    }
}