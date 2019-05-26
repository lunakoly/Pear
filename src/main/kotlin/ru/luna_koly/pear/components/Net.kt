package ru.luna_koly.pear.components

import ru.luna_koly.pear.util.Logger
import ru.luna_koly.pear.events.*
import ru.luna_koly.json.JsonParser
import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.Protocol
import ru.luna_koly.pear.net.connection.Connection
import tornadofx.Controller
import java.net.InetSocketAddress
import java.nio.channels.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Performs network requests when corresponding
 * events are received via EventBus and runs
 * an incoming events handler on a separate
 * background thread.
 *
 * It's marked as Controller() merely
 * to allow interaction with EventBus
 */
class Net : Controller(), Runnable {
    companion object {
        const val DEFAULT_PORT = 1234
    }

    /**
     * They will be registered on the
     * next Net thread loop cycle
     */
    private val registerQueue = LinkedList<SocketChannel>()

    /**
     * Used to pass the received data to the
     * proper receiver
     */
    private val connections = HashMap<SocketChannel, ProfileConnector>()

    /**
     * Adds socket to the register queue
     * so that it'll be registered as soon
     * as current selector events are processed
     */
    private fun register(socket: SocketChannel, profileConnector: ProfileConnector) {
        synchronized(registerQueue) {
            registerQueue.add(socket)
        }

        synchronized(connections) {
            connections[socket] = profileConnector
        }
    }

    /**
     * Manages sockets both created via incoming
     * connection requests and outgoing ones
     */
    private val selector: Selector = Selector.open()
    private val serverSocket: ServerSocketChannel = ServerSocketChannel.open()

    /**
     * Registers all sockets within the selector
     * and clears registerQueue
     */
    private fun registerPendingSockets() {
        synchronized(registerQueue) {
            registerQueue.forEach {
                it.configureBlocking(false)
                it.register(selector, SelectionKey.OP_READ)
            }
            registerQueue.clear()
        }
    }

    /**
     * Basic setup
     */
    private fun configureServer() {
        serverSocket.configureBlocking(false)
        serverSocket.socket().bind(InetSocketAddress(DEFAULT_PORT))
        serverSocket.register(selector, SelectionKey.OP_ACCEPT)
        Logger.log(
            "Net",
            "Server started on port `$DEFAULT_PORT`"
        )
    }

    /**
     * Runs selector and manages its events
     */
    override fun run() {
        configureServer()

        while (serverSocket.isOpen) {
            registerPendingSockets()

            if (selector.selectNow() == 0)
                continue

            val selection = selector.selectedKeys()

            for (that in selection) {
                when {
                    that.isAcceptable -> onClientAccepted((that.channel() as ServerSocketChannel).accept())
                    that.isReadable -> onDataReceived(that.channel() as SocketChannel)
                }
            }

            selection.clear()
        }
    }

    private fun onClientAccepted(socket: SocketChannel) {
        val connection = Connection(socket)
        val profileConnector = Protocol.acceptClient(connection)

        if (profileConnector != null) {
            Logger.log(
                "Net",
                "Server accepted new socket from address `${socket.socket().inetAddress.hostName}`"
            )

            register(socket, profileConnector)
            fire(ConnectionEstablishedEvent(profileConnector))
        }
    }

    private fun onDataReceived(socket: SocketChannel) {
        val profileConnector: ProfileConnector

        synchronized(connections) {
            profileConnector = connections[socket] ?: return
        }

        val connection = profileConnector.lastBoundConnection ?: return

        try {
            val content = JsonParser.parse(connection.readString())
            val messageEvent = MessageEvent(content["text"].value)
            fire(messageEvent)
        } catch (e: Exception) {
            Logger.log("Net", "Error > Invalid message received")
        }
    }

    init {
        subscribe<ConnectionRequest> {
            onConnectionRequested(it)
        }
    }

    private fun onConnectionRequested(event: ConnectionRequest) {
        val socket =  SocketChannel.open()
        val connection = Connection(socket)

        try {
            socket.connect(InetSocketAddress(event.address, DEFAULT_PORT))
        } catch (e: UnresolvedAddressException) {
            Logger.log("Net", "Address `${event.address}` could not be resolved")
        }

        Logger.log(
            "Net",
            "Client requested > Address `${event.address}` and port `$DEFAULT_PORT`"
        )

        val profileConnector = Protocol.acceptServer(connection)

        if (profileConnector != null) {
            register(socket, profileConnector)
            fire(ConnectionEstablishedEvent(profileConnector))
        }
    }
}