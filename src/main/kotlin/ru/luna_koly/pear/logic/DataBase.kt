package ru.luna_koly.pear.logic

import ru.luna_koly.pear.events.UpdateMessagesEvent
import ru.luna_koly.pear.net.cryptor.ClientCryptor
import ru.luna_koly.pear.net.cryptor.Cryptor
import tornadofx.Controller
import java.io.File
import java.security.PublicKey

/**
 * Provides information about persons and messages
 */
class DataBase : Controller() {
    private val profiles = ArrayList<Profile>()
    private val profileConnectors = ArrayList<ProfileConnector>()
    private val receivedMessages = HashMap<Person, MutableList<Message>>()

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
     * Returns a copy of all known profiles
     */
    fun getProfiles(): List<Profile> {
        synchronized(profiles) {
            return profiles.toList()
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

    /**
     * Returns a copy of all available
     * ProfileConnectors
     */
    fun getProfileConnectors(): List<ProfileConnector> {
        synchronized(profileConnectors) {
            return profileConnectors.toList()
        }
    }

    /**
     * Adds message to the database
     */
    fun addMessage(other: Person, message: Message) {
        synchronized(receivedMessages) {
            var list = receivedMessages[other]

            if (list == null) {
                list = mutableListOf()
                receivedMessages[other] = list
            }

            synchronized(list) {
                list.add(message)
            }
        }

        fire(UpdateMessagesEvent(other))
    }

    fun getMessagesFor(person: Person): List<Message> {
        synchronized(receivedMessages) {
            val list = receivedMessages[person] ?: return emptyList()

            synchronized(list) {
                return list.toList()
            }
        }
    }

    /**
     * Stores user information
     */
    val user: Person

    /**
     * Used to construct user Person instance
     */
    val cryptor: Cryptor = ClientCryptor()

    init {
        // some actions to read existing user info
        val pearDirectory = File(System.getProperty("user.home"), ".pear")

        if (!pearDirectory.isDirectory)
            pearDirectory.mkdir()

        val settingsFile = File(pearDirectory, "preferences.json")

        if (settingsFile.isFile) {
            // TODO: parse user settings & read add h2 database functionality here
        }

        // create user instance
        val userProfile = Profile(cryptor.getIdentity())
        profiles.add(userProfile)
        user = userProfile
    }
}