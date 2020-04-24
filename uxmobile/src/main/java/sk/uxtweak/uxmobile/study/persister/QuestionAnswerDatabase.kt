package sk.uxtweak.uxmobile.study.persister

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * Created by Kamil Macek on 14.4.2020.
 */
@Database(entities = [QuestionAnswerEntity::class], version = 1, exportSchema = true)
abstract class QuestionAnswerDatabase : RoomDatabase() {
    abstract fun questionAnswerDao(): QuestionAnswerDao

    companion object {
        fun create(context: Context): QuestionAnswerDatabase {
            return Room.databaseBuilder(
                    context,
                    QuestionAnswerDatabase::class.java,
                    "AnswerQuestionDatabase"
                )
                .build()
        }
    }
}
