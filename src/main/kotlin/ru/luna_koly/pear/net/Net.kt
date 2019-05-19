package ru.luna_koly.pear.net

import ru.luna_koly.pear.Logger
import ru.luna_koly.pear.events.ConnectionEstablishedEvent
import ru.luna_koly.pear.events.ConnectionRequest
import ru.luna_koly.pear.events.DataReceivedEvent
import ru.luna_koly.pear.events.ServerStartRequest
import tornadofx.Controller
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.channels.*

/**
 * Performs network requests when corresponding
 * events are received via EventBus and runs
 * an incoming events handler on a separate
 * background thread.
 *
 * It's marked as Controller() merely
 * to allow interaction with EventBus
 */
class Net : Controller() {
    companion object {
        const val DEFAULT_PORT = 1234
        const val CHUNK_CAPACITY = 48
    }

    /**
     * Manages sockets both created via incoming
     * connection requests and outgoing ones
     */
    private val selector: Selector = Selector.open()
    private val serverSocket: ServerSocketChannel = ServerSocketChannel.open()

    private val serverThread = Thread {
        // configure server
        serverSocket.configureBlocking(false)
        serverSocket.socket().bind(InetSocketAddress(DEFAULT_PORT))
        serverSocket.register(selector, SelectionKey.OP_ACCEPT)
        Logger.log(
            "Net",
            "Server started on port `$DEFAULT_PORT`"
        )

        // run server checkout loop
        while (serverSocket.isOpen) {
            selector.select()
            val selection = selector.selectedKeys()

            for (that in selection)
                when {
                    that.isAcceptable -> onClientAccepted(that)
                    that.isReadable -> onDataReceived(that)
                }

            selection.clear()
        }
    }

    private fun onClientAccepted(it: SelectionKey) {
        val socket = (it.channel() as ServerSocketChannel).accept()
        val connection = Connection(socket)

        Protocol.onClientAccepted(connection)

        socket.configureBlocking(false)
        socket.register(it.selector(), SelectionKey.OP_READ)
        Logger.log(
            "Net",
            "Server accepted new socket from address `${socket.socket().inetAddress.hostName}`"
        )

        fire(ConnectionEstablishedEvent(connection))
    }

    private fun onDataReceived(it: SelectionKey) {
        val socket = it.channel() as SocketChannel
        fire(DataReceivedEvent(Connection(socket)))
    }

    init {
        subscribe<ConnectionRequest> {
            onConnectionRequested(it)
        }

        subscribe<ServerStartRequest> {
            onServerStartRequested()
        }
    }

    private fun onServerStartRequested() {
        serverThread.name = "Net Thread"
        serverThread.isDaemon = true
        serverThread.start()
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

        Protocol.onServerAccepted(connection)

        socket.configureBlocking(false)
        socket.register(selector, SelectionKey.OP_READ)

        fire(ConnectionEstablishedEvent(connection))
    }
}