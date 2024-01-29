package br.com.leandro.fichaleitura.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.leandro.fichaleitura.data.model.Record

@Entity(

    tableName = "record",

    indices = [
        Index(value = ["id_book"])
    ],

    foreignKeys = [
        ForeignKey (
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_book"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.CASCADE
        )
    ]

)
data class RecordEntity (

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "id_book")
    var idBook: String,

    @ColumnInfo(name = "begin_date")
    var beginDate: Long,

    @ColumnInfo(name = "end_date")
    var endDate: Long?,

    @ColumnInfo(name = "notes")
    var notes: String?,

    @ColumnInfo(name = "registration_date")
    var registrationDate: Long,

    @ColumnInfo(name = "last_update_date")
    var lastUpdateDate: Long,

    @ColumnInfo(name = "reading_completed")
    var readingCompleted: Boolean,

    @ColumnInfo(name = "is_deleted")
    var isDeleted: Boolean

) {

    fun toReadingRecord(): Record {
        return with (this) {
            Record(
                id,
                idBook,
                beginDate,
                endDate,
                notes,
                registrationDate,
                lastUpdateDate,
                readingCompleted,
                isDeleted
            )
        }
    }

}