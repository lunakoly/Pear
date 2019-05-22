package ru.luna_koly.pear

import java.io.File

object DataBase {
    val profiles = ArrayList<Profile>()

    init {
        val pearDirectory = File(System.getProperty("user.home"), ".pear")

        if (!pearDirectory.isDirectory)
            pearDirectory.mkdir()

        val settingsFile = File(pearDirectory, "preferences.json")

        if (settingsFile.isFile) {

        }
    }
}