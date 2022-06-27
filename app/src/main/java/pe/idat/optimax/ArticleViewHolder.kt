package pe.idat.optimax

import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pe.idat.optimax.databinding.ItemArticleBinding

class ArticleViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemArticleBinding.bind(view)

    fun bind(article:ArticleResponse){

        Picasso.get().load(article.imagen).into(binding.ivImage)
        binding.tvPrice.text = article.price
    }
}