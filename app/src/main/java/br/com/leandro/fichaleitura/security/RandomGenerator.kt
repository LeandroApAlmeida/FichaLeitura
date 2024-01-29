package br.com.leandro.fichaleitura.security

import java.security.SecureRandom
import java.util.UUID

object RandomGenerator {


    private val random = SecureRandom()


    fun getUUID(): String {
        return UUID.randomUUID().toString()
    }


    fun getBytes(length: Int, seed: ByteArray): ByteArray {
        val randomBytes = ByteArray(length)
        random.setSeed(seed)
        random.nextBytes(randomBytes)
        return randomBytes
    }


    fun getBytes(length: Int): ByteArray {
        return getBytes(length, random.generateSeed(32))
    }


}