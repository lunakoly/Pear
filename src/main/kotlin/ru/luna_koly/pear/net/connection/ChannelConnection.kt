package ru.luna_koly.pear.net.connection

import ru.luna_koly.pear.util.ByteCache
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel

/**
 * Allows to easily analyze data from
 * ByteChannel and write to it
 */
class ChannelConnection(private val channel: ByteChannel) : Connection {
    companion object {
        const val CHUNK_CAPACITY: Byte = 48
    }

    override var protector: Protector = TrivialProtector()

    /**
     * Sends a package containing parameters byte and
     * part of data described via offset and length
     */
    private fun sendRawChunk(data: ByteArray, offset: Int, length: Byte, parameters: Int) {
        val buffer = ByteBuffer.allocate(2 + length)
        buffer.put(length)
        buffer.put(parameters.toByte())
        buffer.put(data, offset, length.toInt())
        buffer.flip()

        do {
            val bytesWritten = channel.write(buffer)
        } while (bytesWritten > 0)
    }

    /**
     * Sends the whole data. It may be spited into
     * pieces and require other side to reconstruct it
     * via their headerCache
     */
    private fun sendRawBytes(data: ByteArray) {
        var offset = 0

        while (offset < data.size - CHUNK_CAPACITY) {
            sendRawChunk(data, offset,
                CHUNK_CAPACITY,
                PackageParameters.HAS_NEXT
            )
            offset += CHUNK_CAPACITY
        }

        sendRawChunk(data, offset, (data.size - offset).toByte(), PackageParameters.NOTHING)
    }

    override fun sendBytes(data: ByteArray) {
        synchronized(channel) {
            sendRawBytes(protector.encrypt(data))
        }
    }

    /**
     * Caches incoming ByteArray data and helps to
     * construct it back into one big ByteArray when
     * no further packages are expected (PackageParameters.HAS_NEXT)
     */
    private val cache = ByteCache()

    /**
     * Reads part of package that contains it's
     * data length
     */
    private fun extractLength(channel: ByteChannel): Int {
        val buffer = ByteBuffer.allocate(1)

        if (channel.read(buffer) <= 0)
            return 0

        buffer.flip()
        return buffer.get().toInt()
    }

    /**
     * Reads part of package that contains it's
     * parameters
     */
    private fun extractParameters(channel: ByteChannel): Int {
        val buffer = ByteBuffer.allocate(1)

        if (channel.read(buffer) <= 0)
            return 0

        buffer.flip()
        return buffer.get().toInt()
    }

    /**
     * Accepts ByteArray coming from the socket. Due to
     * the fact that data may be passed as several packages,
     * it will automatically be reconstructed via headerCache
     */
    private fun readRawBytes(): ByteArray {
        var headerLength = 0
        cache.clear()

        do {
            val length = extractLength(channel)
            val parameters = extractParameters(channel)
            val buffer = ByteBuffer.allocate(length)

            // if got something
            val bytesRead = channel.read(buffer)

            if (bytesRead <= 0)
                break

            buffer.flip()

            // put data into a list ByteArray item
            val data = ByteArray(buffer.limit())
            var index = 0

            while (buffer.hasRemaining()) {
                data[index] = buffer.get()
                index++
            }

            headerLength += data.size
            cache.add(data)

            // if there's something further
        } while (parameters.provide(PackageParameters.HAS_NEXT))

        // bring list items together into `header`
        return cache.concat()
    }

    override fun readBytes(): ByteArray {
//        println("===================< RECEIVED >===================")
//        println("[RAW]: " + raw.toString(Charsets.UTF_8))
//        println("[DECODED]: " + data.toString(Charsets.UTF_8))
//        println("")
        synchronized(channel) {
            return protector.decrypt(readRawBytes())
        }
    }
}