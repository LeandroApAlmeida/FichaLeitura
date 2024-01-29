package br.com.leandro.fichaleitura.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.leandro.fichaleitura.data.model.Book

@Entity(tableName = "book")
data class BookEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "subtitle")
    var subtitle: String?,

    @ColumnInfo(name = "author")
    var author: String,

    @ColumnInfo(name = "publisher")
    var publisher: String,

    @ColumnInfo(name = "isbn")
    var isbn: String,

    @ColumnInfo(name = "edition")
    var edition: Int,

    @ColumnInfo(name = "volume")
    var volume: Int,

    @ColumnInfo(name = "release_year")
    var releaseYear: Int,

    @ColumnInfo(name = "cover", typeAffinity = ColumnInfo.BLOB)
    var cover: ByteArray?,

    @ColumnInfo(name = "summary")
    var summary: String?,

    @ColumnInfo(name = "file_path")
    var filePath: String?,

    @ColumnInfo(name = "registration_date")
    var registrationDate: Long,

    @ColumnInfo(name = "last_update_date")
    var lastUpdateDate: Long
    
) {

    fun toBook(): Book {
        return with (this) {
            Book(
                id,
                title,
                subtitle,
                author,
                publisher,
                isbn,
                edition,
                volume,
                releaseYear,
                cover,
                summary,
                filePath,
                registrationDate,
                lastUpdateDate
            )
        }
    }

}