package br.com.leandro.fichaleitura.image

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream


object Photo {


    fun toBitmap(imageStream: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(
            imageStream,
            0,
            imageStream.size
        )
    }


    fun toBitmap(context: Context, uri: Uri): Bitmap? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        return if (cursor!= null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            cursor.close()
            BitmapFactory.decodeFile(path)
        } else {
            null
        }
    }


    fun toStream(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


}