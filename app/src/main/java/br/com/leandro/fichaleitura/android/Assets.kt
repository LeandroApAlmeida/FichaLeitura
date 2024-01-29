package br.com.leandro.fichaleitura.android

object Assets {


    fun getKeyHash(): String {
        val keyBase64: String
        Application.getInstance().assets.open("key_hash").use { inputStream ->
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            keyBase64 = String(buffer)
        }
        return keyBase64
    }


}