package pe.idat.optimax.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pe.idat.optimax.model.ArticleEntity

@Database(entities=arrayOf(ArticleEntity::class),version=2)
abstract class OptimaxDatabase: RoomDatabase()
{
    abstract fun OptimaxDao(): OptimaxDao
}