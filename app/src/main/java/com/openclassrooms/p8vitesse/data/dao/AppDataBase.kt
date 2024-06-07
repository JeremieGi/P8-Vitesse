package com.openclassrooms.p8vitesse.data.dao

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import com.openclassrooms.p8vitesse.data.entity.RoomConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date

@Database(entities = [CandidateDto::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDataBase : RoomDatabase(){

    // Dao access
    abstract fun candidateDao(): CandidateDao

    private class AppDatabaseCallback(

        private val scope: CoroutineScope

    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    initDatabase(database.candidateDao())
                }
            }
        }

        suspend fun initDatabase(candidateDao: CandidateDao) {

            // Test candidates

            val currentDate = Calendar.getInstance()
            currentDate.set(1980, 0, 1)

            for (i in 0 until 9) {

                // A favorite for all 3 candidates
                var bFavorite = (i%3) == 0

                val candidateTest = CandidateDto(
                    lastName = "LastName$i",
                    firstName = "FirstName$i",
                    phone = "06.12.34.35.3$i",
                    email = "firstname$i.lastname$i@free.fr",
                    dateOfBirth = currentDate.time,
                    salaryExpectation = 3000+(i*100),
                    note = "note$i /n Contenu de la note",
                    topFavorite = bFavorite
                )

                // Add one year
                currentDate.add(Calendar.DAY_OF_YEAR, 1)

            }


        }
    }

    companion object {
        @Volatile // utilisée en Kotlin pour indiquer qu'une propriété peut être modifiée par plusieurs threads
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDataBase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "VitesseDB"
                )
                    .addCallback(AppDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}