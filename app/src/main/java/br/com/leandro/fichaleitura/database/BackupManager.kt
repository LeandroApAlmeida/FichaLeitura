package br.com.leandro.fichaleitura.database

import android.net.Uri
import androidx.core.net.toUri
import br.com.leandro.fichaleitura.android.Assets
import br.com.leandro.fichaleitura.android.FilesManager
import br.com.leandro.fichaleitura.android.Application
import br.com.leandro.fichaleitura.security.AESCipher
import br.com.leandro.fichaleitura.security.CipherListener
import br.com.leandro.fichaleitura.security.HashGenerator
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.getSystemTime
import br.com.leandro.fichaleitura.utils.hourToText
import br.com.leandro.fichaleitura.utils.stringBase64ToByteArray
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class BackupManager {


    private val databaseFile: File = AppDatabase.getFile()


    private fun getPasswordHash(): ByteArray {
        val key = Assets.getKeyHash()
        val hashWithSalt: ByteArray = stringBase64ToByteArray(key)
        return HashGenerator.extractHashBytes(hashWithSalt)
    }


    fun doBackup(backupFileUri: Uri, cipherListener: CipherListener? = null) {

        val databaseFolder = databaseFile.parent?.let { File(it) }

        val date = Date(getSystemTime())
        val dateFields = dateToText(date).split("/")
        val dateStr = dateFields[2] + dateFields[1] + dateFields[0]
        val hourStr = hourToText(date).replace(":", "")
        val zipFileName = "zip_stream_${dateStr}_${hourStr}.zip"
        val zipFile = FilesManager.getCacheFile(zipFileName)

        zipAll(zipFile, databaseFolder!!)

        val contentResolver = Application.getInstance().contentResolver
        val sourcePfd = contentResolver.openFileDescriptor(zipFile.toUri(), "r")
        val targetPfd = contentResolver.openFileDescriptor(backupFileUri, "w")
        val inputStream = FileInputStream(sourcePfd!!.fileDescriptor)
        val outputStream = FileOutputStream(targetPfd!!.fileDescriptor)
        val hash = getPasswordHash()

        AESCipher.encrypt(inputStream, outputStream, hash, cipherListener)

        zipFile.delete()
        sourcePfd.close()
        targetPfd.close()

    }


    fun doRestore(restoreFileUri: Uri, cipherListener: CipherListener? = null) {

        val date = Date(getSystemTime())
        val dateFields = dateToText(date).split("/")
        val dateStr = dateFields[2] + dateFields[1] + dateFields[0]
        val hourStr = hourToText(date).replace(":", "")

        val zipFileName = "backup_${dateStr}_${hourStr}.zip"
        val tmpFile = File(FilesManager.getAppDirectory()!!.absolutePath, zipFileName)
        FilesManager.copyFile(restoreFileUri, tmpFile.toUri())

        val extractedFileName = "backup_${dateStr}_${hourStr}.tmp"
        val zipFile = File(FilesManager.getAppDirectory()!!.absolutePath, extractedFileName)
        val contentResolver = Application.getInstance().contentResolver
        val sourcePfd = contentResolver.openFileDescriptor(restoreFileUri, "r")
        val targetPfd = contentResolver.openFileDescriptor(zipFile.toUri(), "w")
        val inputStream = FileInputStream(sourcePfd!!.fileDescriptor)
        val outputStream = FileOutputStream(targetPfd!!.fileDescriptor)
        val hash = getPasswordHash()

        AESCipher.decrypt(inputStream, outputStream, hash, cipherListener)

        unzip(zipFile, File(AppDatabase.getFile().parent))

        zipFile.delete()
        sourcePfd.close()
        targetPfd.close()

    }


    private fun zipAll(zipFile: File, sourceFolder: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            sourceFolder.walkTopDown().forEach { file ->
                val zipFileName = file.absolutePath.removePrefix(sourceFolder.absolutePath).removePrefix("/")
                val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
                zos.putNextEntry(entry)
                if (file.isFile) {
                    file.inputStream().copyTo(zos)
                }
            }
        }
    }


    private fun unzip(zipFile: File, destinationFolder: File) {
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    if (!entry.isDirectory) {
                        val filePath = destinationFolder.absolutePath + "/" + entry.name
                        File(filePath).outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }


}