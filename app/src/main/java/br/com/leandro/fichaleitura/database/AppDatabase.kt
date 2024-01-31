package br.com.leandro.fichaleitura.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.leandro.fichaleitura.android.Application
import br.com.leandro.fichaleitura.database.dao.BookDao
import br.com.leandro.fichaleitura.database.dao.RecordDao
import br.com.leandro.fichaleitura.database.entity.BookEntity
import br.com.leandro.fichaleitura.database.entity.RecordEntity
import financial.mobile.database.Migrations
import java.io.File


/**
 * Classe de acesso ao banco de dados do aplicativo.
 */
@Database(

    version = 1,

    entities = [
        BookEntity::class,
        RecordEntity::class
    ],

    /*autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 8, to = 9)
    ],*/

    exportSchema = true

)
abstract class AppDatabase: RoomDatabase() {


    abstract fun getBookDao(): BookDao

    abstract fun getRecordDao(): RecordDao


    fun disconnect() {
        if (this.isOpen) {
            this.close()
        }
    }


    companion object {


        private const val DATABASE_NAME = "reading_record_db"


        @Volatile
        private var INSTANCE: AppDatabase = Room.databaseBuilder(
            Application.getInstance(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(*Migrations.migrations).build()


        val instance: AppDatabase
            get() {
            synchronized(this) {
                return INSTANCE
            }
        }


        fun getFile(): File = File(Application.getInstance().getDatabasePath(DATABASE_NAME).absolutePath)


    }

}