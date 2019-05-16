package ru.luna_koly.pear

import ru.luna_koly.pear.events.ConnectionEstablishedEvent
import ru.luna_koly.pear.events.ConnectionRequest
import ru.luna_koly.pear.events.DataReceivedEvent
import ru.luna_koly.pear.events.ServerStartRequest
import tornadofx.Controller
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets
import java.util.*

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

    /**
     * Caches incoming ByteArray data and helps to
     * construct it back into one big ByteArray when
     * no further packages are expected (NetParameters.HAS_NEXT)
     */
    private val headerCache = mutableMapOf<SocketChannel, LinkedList<ByteArray>>()

    private val serverThread = Thread {
        // configure server
        serverSocket.configureBlocking(false)
        serverSocket.socket().bind(InetSocketAddress(DEFAULT_PORT))
        serverSocket.register(selector, SelectionKey.OP_ACCEPT)
        Logger.log("Net", "Server started on port `$DEFAULT_PORT`")

        // run server checkout loop
        while (serverSocket.isOpen) {
            if (selector.selectNow() == 0)
                continue

            val selection = selector.selectedKeys()

            for (that in selection)
                when {
                    that.isAcceptable -> onClientAccepted(that)
                    that.isConnectable -> onConnectionEstablished(that)
                    that.isReadable -> onDataReceived(that)
                }

            selection.clear()
        }
    }

    /**
     * Sends a package containing parameters byte and
     * part of data described via offset and length
     */
    private fun sendChunk(socket: SocketChannel, data: ByteArray, offset: Int, length: Int, parameters: Int) {
        val buffer = ByteBuffer.allocate(length + 1)
        buffer.put(parameters.toByte())
        buffer.put(data, offset, length)
        buffer.flip()

        do {
            val bytesWritten = socket.write(buffer)
        } while (bytesWritten > 0)
    }

    /**
     * Sends the whole data. It may be spited into
     * pieces and require other side to reconstruct it
     * via their headerCache
     */
    private fun send(socket: SocketChannel, data: ByteArray) {
        var offset = 0

        while (offset < data.size - CHUNK_CAPACITY) {
            sendChunk(socket, data, offset, CHUNK_CAPACITY, NetParameters.HAS_NEXT)
            offset += CHUNK_CAPACITY
        }

        sendChunk(socket, data, offset, data.size - offset, NetParameters.NOTHING)
    }

    private fun onClientAccepted(it: SelectionKey) {
        val socket = (it.channel() as ServerSocketChannel).accept()
        socket.configureBlocking(false)
        socket.register(it.selector(), SelectionKey.OP_READ or SelectionKey.OP_CONNECT)
        Logger.log("Net", "Server accepted new socket from address `${socket.socket().inetAddress.hostName}`")

        send(socket, "Hello!".toByteArray(StandardCharsets.UTF_8))
        Logger.log("Net", "Server sent `Hello!`")
    }

    private fun onConnectionEstablished(it: SelectionKey) {
        val socket = it.channel() as SocketChannel
        socket.finishConnect()
        Logger.log("Net", "Connection established on ${socket.socket().inetAddress.hostName}")
        fire(ConnectionEstablishedEvent(it))
    }

    /**
     * Accepts ByteArray coming from the socket. Due to
     * the fact that data may be passed as several packages,
     * it will automatically be reconstructed via headerCache
     */
    private fun readHeader(socket: SocketChannel): ByteArray {
        val buffer = ByteBuffer.allocate(CHUNK_CAPACITY + 1)
        var headerLength = 0

        do {
            // if got something
            val bytesRead = socket.read(buffer)

            if (bytesRead <= 0)
                break

            buffer.flip()

            // accept parameters & other data
            val parameters = buffer.get().toInt()
            var socketHeaderCache = headerCache[socket]

            if (socketHeaderCache == null) {
                socketHeaderCache = LinkedList()
                headerCache[socket] = socketHeaderCache
            }

            // put data into a list ByteArray item
            val data = ByteArray(buffer.limit() - 1)
            var index = 0

            while (buffer.hasRemaining()) {
                data[index] = buffer.get()
                index++
            }

            headerLength += data.size
            socketHeaderCache.add(data)
            buffer.clear()

            // if there's something further
        } while (parameters.provide(NetParameters.HAS_NEXT))

        // bring list items together into `header`
        val header = ByteArray(headerLength)
        val socketHeaderCache = headerCache[socket]
        var index = 0

        // if `bytesRead <= 0` would fail then there might be no
        // corresponding ByteArray list at all!
        if (socketHeaderCache != null) {
            for (it in socketHeaderCache) {
                System.arraycopy(it, 0, header, index, it.size)
                index += it.size
            }
        }

        return header
    }

    private fun onDataReceived(it: SelectionKey) {
        val socket = it.channel() as SocketChannel
        fire(DataReceivedEvent(readHeader(socket)))
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
        socket.configureBlocking(false)
        socket.register(selector, SelectionKey.OP_READ or SelectionKey.OP_CONNECT)
        socket.connect(InetSocketAddress(event.address, DEFAULT_PORT))
        Logger.log("Net", "Client started for address `${event.address}` and port `$DEFAULT_PORT`")
    }
}