package br.com.leandro.fichaleitura.security

import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCipher {


    private const val TRANSFORMATION = "AES/CFB/PKCS7PADDING"
    private const val ALGORITHM = "AES"
    private const val IV_SIZE = 16
    private const val BUFFER_SIZE = 4096
    private const val KEY_SIZE = 32


    fun encrypt(inputStream: InputStream, outputStream: OutputStream, keyBytes: ByteArray,
    listener: CipherListener? = null) {

        if (keyBytes.size < KEY_SIZE) throw Exception("Invalid key_hash size. Use 32 bytes key_hash length")

        listener?.onStartProcess( inputStream.available() )

        try {

            val ivBytes = RandomGenerator.getBytes(IV_SIZE)
            val iv = IvParameterSpec(ivBytes)
            val keySpec = SecretKeySpec(keyBytes, ALGORITHM)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)

            outputStream.use { output ->

                output.write(ivBytes)

                listener?.onProcessData(ivBytes.size)

                inputStream.use { input ->

                    CipherOutputStream(output, cipher).use { outputStream ->

                        val buffer = ByteArray(BUFFER_SIZE)
                        var length: Int

                        while (input.read(buffer).also { length = it } > 0) {

                            outputStream.write(buffer, 0, length)

                            listener?.onProcessData(length)

                        }

                        outputStream.flush()

                    }

                }

            }

        } finally {
            listener?.onEndProcess(0)
        }

    }


    fun decrypt(inputStream: InputStream, outputStream: OutputStream, keyBytes: ByteArray,
    listener: CipherListener? = null) {

        if (keyBytes.size < KEY_SIZE) throw Exception("Invalid key_hash size. Use 32 bytes key_hash length")

        listener?.onStartProcess( inputStream.available() )

        try {

            val keySpec = SecretKeySpec(keyBytes, ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)

            outputStream.use { output ->

                inputStream.use { input ->

                    val ivBytes = ByteArray(IV_SIZE)
                    input.read(ivBytes)

                    listener?.onProcessData(IV_SIZE)

                    val iv = IvParameterSpec(ivBytes)

                    cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)

                    CipherOutputStream(output, cipher).use { outputStream ->

                        val buffer = ByteArray(BUFFER_SIZE)
                        var length: Int

                        while (input.read(buffer).also { length = it } > 0) {

                            outputStream.write(buffer, 0, length)

                            listener?.onProcessData(length)

                        }

                        outputStream.flush()

                    }

                }

            }

        } finally {
            listener?.onEndProcess(0)
        }

    }

}