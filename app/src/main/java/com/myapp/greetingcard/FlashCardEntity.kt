package com.myapp.greetingcard

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "FlashCards",
    indices = [Index(value = ["english_card","vietnamese_card"],
    unique = true)])
data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "english_card") val englishCard: String?, //?: can be null
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)

