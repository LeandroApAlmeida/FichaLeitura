package br.com.leandro.fichaleitura.security

import java.security.MessageDigest

object HashGenerator {

    private const val SALT_SIZE: Int = 32

    fun generateSHA256Hash(dataBytes: ByteArray, saltBytes: ByteArray): ByteArray {
        val dataAndSalt = saltBytes + dataBytes
        return MessageDigest.getInstance("SHA-256").digest(dataAndSalt)
    }

    fun generateSHA256HashWithSalt(dataBytes: ByteArray): ByteArray {
        val saltBytes = RandomGenerator.getBytes(SALT_SIZE)
        val hash = generateSHA256Hash(dataBytes, saltBytes)
        return saltBytes + hash
    }

    fun extractSaltBytes(hashWithSalt: ByteArray): ByteArray {
        return hashWithSalt.copyOfRange(0, SALT_SIZE)
    }

    fun extractHashBytes(hashWithSalt: ByteArray): ByteArray {
        return hashWithSalt.copyOfRange(SALT_SIZE, hashWithSalt.size)
    }

}