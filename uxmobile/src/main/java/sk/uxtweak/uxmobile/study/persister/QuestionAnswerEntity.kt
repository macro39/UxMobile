package sk.uxtweak.uxmobile.study.persister

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Kamil Macek on 14.4.2020.
 */
@Entity(
    tableName = "question_answer"
)
data class QuestionAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "question_answer") val questionAnswer: String
)
