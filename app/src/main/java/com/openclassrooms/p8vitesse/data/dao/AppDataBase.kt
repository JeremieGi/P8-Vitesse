package com.openclassrooms.p8vitesse.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import com.openclassrooms.p8vitesse.data.entity.RoomConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [CandidateDto::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDataBase : RoomDatabase(){

    // Dao access
    abstract fun candidateDao(): CandidateDao

    // TODO : Comment on fait pour mettre à jour une base existante ?

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

        /**
         * Init the candidates database
         */
        suspend fun initDatabase(candidateDao: CandidateDao) {

            // Generated test candidates

            val currentDate = Calendar.getInstance()
            currentDate.set(1980, 0, 1)

            // Create 9 candidates
            for (i in 1 until 100) {

                // A favorite for all 3 candidates
                val bFavorite = (i%3) == 0

                val candidateTest = CandidateDto(
                    lastName = "LastName$i",
                    firstName = "FirstName$i",
                    phone = "06.12.34.35.${String.format("%02d", i)}",
                    email = "firstname$i.lastname$i@free.fr",
                    dateOfBirth = currentDate.time,
                    salaryExpectation = 3000+(i*100),
                    note = "note$i \nDuplexque isdem diebus acciderat malum, quod et Theophilum insontem atrox interceperat casus",
                    topFavorite = bFavorite/*,
                    photoFilePath = ""*/
                )

                // Insert in the DB
                candidateDao.insertCandidate(candidateTest)

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