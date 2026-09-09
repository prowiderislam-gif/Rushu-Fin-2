package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FinanceDao
import com.example.data.model.AppStateEntity
import com.example.data.model.LiabilityEntity
import com.example.data.model.TransactionEntity

@Database(
    entities = [TransactionEntity::class, LiabilityEntity::class, AppStateEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        const val DATABASE_NAME = "rushu_fin.db"

        // Adds the "category" column to existing installs without wiping
        // any real transaction data already on the device.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN category TEXT NOT NULL DEFAULT 'Uncategorized'")
            }
        }

        // Adds the "showLiabilities" toggle column, defaulting to visible (1)
        // so existing installs keep seeing liabilities unless they turn it off.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE app_state ADD COLUMN showLiabilities INTEGER NOT NULL DEFAULT 1")
            }
        }

        // Adds the "themeMode" column ("DEFAULT" / "BASIC" / "KITTY"),
        // defaulting to the original look so existing installs are unaffected
        // unless they pick a different theme.
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE app_state ADD COLUMN themeMode TEXT NOT NULL DEFAULT 'DEFAULT'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
