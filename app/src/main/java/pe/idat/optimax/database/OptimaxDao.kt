package pe.idat.optimax.database

import androidx.room.*
import pe.idat.optimax.model.ArticleEntity

@Dao
interface OptimaxDao
{
    @Insert
    fun insertDB(articleEntity: ArticleEntity)

    @Update
    fun updateDB(articleEntity: ArticleEntity)

    @Delete
    fun deleteDB(articleEntity: ArticleEntity)

    @Query("SELECT * FROM ArticleTable WHERE articleId IN (:articleId)")
    fun findByIdDB(articleId:Int): ArticleEntity

    @Query("SELECT * FROM ArticleTable")
    fun findAllDB(): MutableList<ArticleEntity>
}