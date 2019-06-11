package ru.luna_koly.pear.logic

import ru.luna_koly.pear.events.UpdateMessagesEvent
import ru.luna_koly.pear.net.cryptor.ClientCryptor
import ru.luna_koly.pear.net.cryptor.Cryptor
import tornadofx.Controller
import java.io.File
import java.security.PublicKey

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
     * Synchronously registers profileConnector
     */
    fun addProfileConnector(profileConnector: ProfileConnector) {
        synchronized(profileConnectors) {
            profileConnectors.add(profileConnector)
        }
    }

    fun getProfiles(): List<Profile> {
        synchronized(profiles) {
            return profiles.toList()
        }
    }

    fun getProfileConnectors(): List<ProfileConnector> {
        synchronized(profileConnectors) {
            return profileConnectors.toList()
        }
    }

    fun addMessage(message: Message) {
        synchronized(receivedMessages) {
            var list = receivedMessages[message.author]

            if (list == null) {
                list = mutableListOf()
                receivedMessages[message.author] = list
            }

            list.add(message)
        }

        fire(UpdateMessagesEvent(message.author))
    }

    fun getMessagesFor(person: Person): List<Message> {
        synchronized(receivedMessages) {
            val list = receivedMessages[person] ?: return emptyList()
            return list.toList()
        }
    }

    val cryptor: Cryptor = ClientCryptor()
    val user: Person

    init {
        val pearDirectory = File(System.getProperty("user.home"), ".pear")

        if (!pearDirectory.isDirectory)
            pearDirectory.mkdir()

        val settingsFile = File(pearDirectory, "preferences.json")

        if (settingsFile.isFile) {

        }

        val userProfile = Profile(cryptor.getIdentity())
        profiles.add(userProfile)
        user = userProfile
    }
}