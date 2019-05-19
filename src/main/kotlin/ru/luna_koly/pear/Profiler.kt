package ru.luna_koly.pear

import java.security.KeyPair
import java.security.KeyPairGenerator

object Profiler {
    private val profiles = ArrayList<Profile>()

    val keyPair = initializeKeyPair()

    private fun initializeKeyPair(): KeyPair {
        val keyGenerator = KeyPairGenerator.getInstance("RSA")
        keyGenerator.initialize(2048)
        return keyGenerator.generateKeyPair()
    }
}