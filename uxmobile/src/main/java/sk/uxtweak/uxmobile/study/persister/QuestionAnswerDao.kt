package sk.uxtweak.uxmobile.study.persister

import androidx.room.*


/**
 * Created by Kamil Macek on 14.4.2020.
 */
@Dao
interface QuestionAnswerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(events: List<QuestionAnswerEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAnswer(events: List<QuestionAnswerEntity>)

    @Delete
    suspend fun delete(events: List<QuestionAnswerEntity>)

    @Delete
    suspend fun deleteItem(questionAnswerEntity: QuestionAnswerEntity)

    @Query("SELECT * FROM question_answer")
    suspend fun getAll(): List<QuestionAnswerEntity>

    @Query("SELECT * FROM question_answer")
    fun getAllAnswers(): List<QuestionAnswerEntity>

}
