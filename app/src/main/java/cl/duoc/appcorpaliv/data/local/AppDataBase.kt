package cl.duoc.appcorpaliv.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RegistroEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun registroDao(): RegistroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                lateinit var instance: AppDataBase
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "corpaliv_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = instance.registroDao()
                                dao.insertarRegistro(RegistroEntity(nombre = "Javiera Soto", tipo = "Alianza inclusión laboral", estado = "Recibido"))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
