package pe.idat.optimax.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ArticleTable")
data class ArticleEntity(@PrimaryKey
                         var articleId: Int,
                         var price: String,
                         var quantity: Int,
                         var image: String
)
