package financial.mobile.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {

    companion object {

        const val DB_VERSION = 1

        val migrations: Array<Migration> = arrayOf (

            /*
            object: Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE loan ADD COLUMN devolution_date INTEGER DEFAULT 0 NOT NULL")
                }
            },

            object: Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE loan ADD COLUMN notes TEXT DEFAULT ''")
                }
            },

            object: Migration(4, 5) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE loan_parcel ADD COLUMN payment_mode INTEGER DEFAULT 1 NOT NULL")
                }
            },

            object: Migration(5, 6) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE loan ADD COLUMN discount REAL DEFAULT 0.0 NOT NULL")
                    database.execSQL("ALTER TABLE loan ADD COLUMN interest REAL DEFAULT 0.0 NOT NULL")
                }
            },

            object: Migration(6, 7) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("""
                        INSERT INTO settings (setting_key, value_string) 
                        VALUES ('app_version_author', 'LEANDRO APARECIDO DE ALMEIDA')
                    """)
                    database.execSQL("""
                        INSERT INTO settings (setting_key, value_string) 
                        VALUES ('app_version_number', '1.0')
                    """)
                    database.execSQL("""
                        INSERT INTO settings (setting_key, value_string) 
                        VALUES ('app_version_date', '04 de novembro de 2023')
                    """)
                }
            },

            object: Migration(7, 8) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("""
                        UPDATE settings SET value_string = 'Leandro Aparecido de Almeida'
                        WHERE setting_key = 'app_version_author'
                    """)
                }
            }

             */

        )

    }

}