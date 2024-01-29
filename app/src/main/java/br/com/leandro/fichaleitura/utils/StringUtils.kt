package br.com.leandro.fichaleitura.utils

fun byteArrayToStringBase64(dataBytes: ByteArray): String {
    return android.util.Base64.encodeToString(dataBytes, android.util.Base64.DEFAULT)
}

fun stringBase64ToByteArray(stringBase64: String): ByteArray {
    return android.util.Base64.decode(stringBase64, android.util.Base64.DEFAULT)
}

fun stringToByteArray(string: String): ByteArray {
    return string.toByteArray()
}