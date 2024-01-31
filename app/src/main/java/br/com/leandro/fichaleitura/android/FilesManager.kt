package br.com.leandro.fichaleitura.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object FilesManager {


    fun getAppDirectory(): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val storageVolume = getVolume(0)
            if (storageVolume.state.equals(Environment.MEDIA_MOUNTED)) {
                val directory = File(storageVolume.directory!!.absolutePath, "/.FichaLeitura")
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                directory
            } else {
                null
            }
        } else {
            null
        }
    }


    private fun getVolume(id: Int): StorageVolume {
        val storeManager: StorageManager = Application.getInstance()
        .getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumes = storeManager.storageVolumes
        return storageVolumes[id]
    }


    fun getCacheFile(fileName: String): File {
        return File(Application.getInstance().applicationContext.cacheDir, fileName)
    }


    fun copyFile(sourceFile: Uri, targetFile: Uri) {
        val contentResolver = Application.getInstance().contentResolver
        val sourcePfd = contentResolver.openFileDescriptor(sourceFile, "r")
        val targetPfd = contentResolver.openFileDescriptor(targetFile, "w")
        FileInputStream(sourcePfd!!.fileDescriptor).use { fin ->
            FileOutputStream(targetPfd!!.fileDescriptor).use { out ->
                val buffer = ByteArray(4096)
                var length: Int
                while (fin.read(buffer).also { length = it } > 0) {
                    out.write(buffer, 0, length)
                }
                out.flush()
                out.close()
                fin.close()
            }
        }
        sourcePfd?.close()
        targetPfd?.close()
    }


    fun openFile(context: Context, file: File) {
        if (file.exists()) {
            val extension = file.name.substring(file.name.lastIndexOf(".") + 1)
            val mime = MimeTypeMap.getSingleton()
            val type = mime.getMimeTypeFromExtension(extension)
            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), type)
            context.startActivity(Intent.createChooser(intent, "Abrir arquivo com"))
            //Application.getInstance().startActivity(intent)
        } else {
            throw Exception("Arquivo inexistente.")
        }
    }


    fun openFile(file: File, fileType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), fileType)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        Application.getInstance().startActivity(intent)
    }


    fun openFile(context: Context, file: File, fileType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), fileType)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }


    fun checkPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(
                        String.format(
                            "package:%s",
                            activity.applicationContext.packageName
                        )
                    )
                    activity.startActivityIfNeeded(intent, 101)
                } catch (ex: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    activity.startActivityIfNeeded(intent, 101)
                }
            }
        }
    }


    fun clearCache() {
        val reportDirectory = getAppDirectory()
        if (reportDirectory != null) {
            if (reportDirectory.exists()) {
                val files = reportDirectory.listFiles()
                try {
                    for (file in files!!) {
                        file.delete()
                    }
                } catch (_: Exception) {
                }
            }
        }
    }


}