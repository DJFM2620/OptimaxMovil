package pe.idat.optimax

import android.app.Application
import androidx.room.Room

import pe.idat.optimax.database.OptimaxDatabase

class OptimaxApplication: Application()
{
    companion object {
        lateinit var database: OptimaxDatabase
    }

    override fun onCreate() {
        super.onCreate()


        //cargar database
        database=Room
            .databaseBuilder(this,OptimaxDatabase::class.java,"OptimaxDatabase")
            .build()
    }
}