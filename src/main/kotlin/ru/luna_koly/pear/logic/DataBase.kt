package ru.luna_koly.pear.logic

import java.io.File
import java.security.PublicKey

object DataBase {
    val profiles = ArrayList<Profile>()
    val profileConnectors = ArrayList<ProfileConnector>()

    /**
     * Synchronously looks for the corresponding
     * profile or inserts new one if no one is found
     */
    fun getProfileFor(identity: PublicKey): Profile {
        synchronized(profiles) {
            var profile = profiles.find { it.identity == identity }

            if (profile != null)
                return profile

            profile = Profile(identity)
            profiles.add(profile)
            return profile
        }
    }

    /**
     * Synchronously registers profileConnector
     */
    fun addProfileConnector(profileConnector: ProfileConnector) {
        synchronized(profileConnectors) {
            profileConnectors.add(profileConnector)
        }
    }

    init {
        val pearDirectory = File(System.getProperty("user.home"), ".pear")

        if (!pearDirectory.isDirectory)
            pearDirectory.mkdir()

        val settingsFile = File(pearDirectory, "preferences.json")

        if (settingsFile.isFile) {

        }
    }
}