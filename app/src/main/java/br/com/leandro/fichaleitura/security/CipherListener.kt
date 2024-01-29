package br.com.leandro.fichaleitura.security

interface CipherListener {

    fun onStartProcess(streamLength: Int)

    fun onProcessData(dataLength: Int)

    fun onEndProcess(status: Int)

}