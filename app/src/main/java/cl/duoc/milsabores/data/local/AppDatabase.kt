package cl.duoc.milsabores.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.milsabores.data.local.dao.CarritoDao
import cl.duoc.milsabores.data.local.dao.PedidoDao
import cl.duoc.milsabores.data.local.entity.CarritoItemEntity
import cl.duoc.milsabores.data.local.entity.PedidoEntity
// RecordatorioEntity está en el mismo package cl.duoc.milsabores.cl.duoc.milsabores.data.local

/**
 * Base de datos SQLite local usando Room
 *
 * @Database: Marca esta clase como base de datos Room
 * entities: Lista de tablas (Entidades)
 * version: Incrementar cuando cambies el esquema de la BD
 * exportSchema: false = no exportar esquema (simplifica en desarrollo)
 */
@Database(
    entities = [
        PedidoEntity::class,
        CarritoItemEntity::class,
        RecordatorioEntity::class
    ],
    version = 2,  // Incrementado porque agregamos nueva tabla
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs (Data Access Objects)
    abstract fun pedidoDao(): PedidoDao
    abstract fun carritoDao(): CarritoDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        // Singleton: una sola instancia de la base de datos en toda la app
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtener la instancia de la base de datos
         * Thread-safe con doble verificación
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mil_sabores_database" // ← Nombre del archivo .db
                )
                    .fallbackToDestructiveMigration() // Si cambias esquema, borra y recrea
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Limpiar la instancia (útil para testing o logout completo)
         */
        fun clearInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

