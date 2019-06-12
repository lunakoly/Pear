package ru.luna_koly.pear.components

import ru.luna_koly.pear.events.ConnectionEstablishedEvent
import ru.luna_koly.pear.events.ConnectionRequest
import ru.luna_koly.pear.events.InvalidAddressEvent
import ru.luna_koly.pear.logic.ProfileConnector
import ru.luna_koly.pear.net.analyzer.Analyser
import ru.luna_koly.pear.net.connection.ChannelConnection
import ru.luna_koly.pear.net.protocol.Protocol
import ru.luna_koly.pear.util.Logger
import tornadofx.Controller
import java.net.InetSocketAddress
import java.nio.channels.*
import java.util.concurrent.ConcurrentLinkedQueue

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
     * Parses incoming data and performs
     * corresponding actions
     */
    internal val analyzer: Analyser by params

    /**
     * Establishes a 'valid' connection
     */
    internal val protocol: Protocol by params

    /**
     * They will be registered on the
     * next Net thread loop cycle.
     * ProfileConnector is used to pass
     * the received data to the
     * proper receiver
     */
    private val registerQueue = ConcurrentLinkedQueue<Pair<SocketChannel, ProfileConnector>>()

    /**
     * Adds socket to the register queue
     * so that it'll be registered as soon
     * as current selector events are processed
     */
    private fun register(socket: SocketChannel, profileConnector: ProfileConnector) {
        registerQueue.add(socket to profileConnector)
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
        registerQueue.forEach {
            it.first.configureBlocking(false)
            val key= it.first.register(selector, SelectionKey.OP_READ)
            key.attach(it.second)
        }
        registerQueue.clear()
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

            if (selector.select(500) == 0)
                continue

            val selection = selector.selectedKeys()

            loop@ for (that in selection) {
                when {
                    !that.isValid -> continue@loop
                    that.isAcceptable -> onClientAccepted(that)
                    that.isReadable -> onDataReceived(that)
                }
            }

            selection.clear()
        }
    }

    private fun onClientAccepted(key: SelectionKey) {
        val socket = (key.channel() as ServerSocketChannel).accept()
        val connection = ChannelConnection(socket)
        val profileConnector = protocol.acceptClient(connection)

        if (profileConnector != null) {
            Logger.log(
                "Net",
                "Server accepted new socket from address `${socket.socket().inetAddress.hostName}`"
            )

            profileConnector.updateInfo()
            register(socket, profileConnector)
            fire(ConnectionEstablishedEvent(profileConnector))
        }
    }

    private fun onDataReceived(key: SelectionKey) {
        val profileConnector = key.attachment() as ProfileConnector
        analyzer.analyze(profileConnector.connection, profileConnector.profile)
    }

    init {
        subscribe<ConnectionRequest> {
            onConnectionRequested(it)
        }
    }

    private fun onConnectionRequested(event: ConnectionRequest) {
        val socket = SocketChannel.open()
        val connection = ChannelConnection(socket)

        try {
            socket.connect(InetSocketAddress(event.address, DEFAULT_PORT))
        } catch (e: UnresolvedAddressException) {
            Logger.log("Net", "Address `${event.address}` could not be resolved")
            fire(InvalidAddressEvent)
            return
        } catch (e: UnsupportedAddressTypeException) {
            Logger.log("Net", "Address `${event.address}` is not supported")
            fire(InvalidAddressEvent)
            return
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.log("Net", "Something happened while connecting to `${event.address}`")
            fire(InvalidAddressEvent)
            return
        }

        Logger.log(
            "Net",
            "Client requested > Address `${event.address}` and port `$DEFAULT_PORT`"
        )

        val profileConnector = protocol.acceptServer(connection)

        Logger.log(
            "Net",
            "Client requested > Server has been accepted"
        )

        if (profileConnector != null) {
            Logger.log(
                "Net",
                "Client requested > profileConnector to server is OK"
            )

            profileConnector.updateInfo()
            register(socket, profileConnector)
            fire(ConnectionEstablishedEvent(profileConnector))
        }
    }
}