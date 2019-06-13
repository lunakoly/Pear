package ru.luna_koly.pear.net.connection

import ru.luna_koly.pear.util.ByteCache
import ru.luna_koly.pear.util.Logger
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.util.*

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

        Logger.log("...", "SENDING: chunk of size $length")
        Logger.log("...", "SENDING: chunk has next ${parameters.provide(PackageParameters.HAS_NEXT)}")

        do {
            val bytesWritten = channel.write(buffer)
            Logger.log("...", "SENDING: bytesWritten $bytesWritten")
        } while (bytesWritten > 0)

        Logger.log("...", "SENDING: chunk sent")
    }

    /**
     * Sends the whole data. It may be spited into
     * pieces and require other side to reconstruct it
     * via their headerCache
     */
    private fun sendRawBytes(data: ByteArray) {
        var offset = 0

        while (offset < data.size - CHUNK_CAPACITY) {
            Logger.log("...", "SENDING: intermediate $offset-${offset + CHUNK_CAPACITY} of ${data.size}")

            sendRawChunk(data, offset,
                CHUNK_CAPACITY,
                PackageParameters.HAS_NEXT
            )
            offset += CHUNK_CAPACITY
        }

        Logger.log("...", "SENDING: last $offset-${offset + CHUNK_CAPACITY} of ${data.size}")
        sendRawChunk(data, offset, (data.size - offset).toByte(), PackageParameters.NOTHING)
    }

    override fun sendBytes(data: ByteArray) {
        synchronized(channel) {
            Logger.log("...", "===================< SENDING >===================")
            Logger.log("...", "[DECODED]: ")
            Logger.log("...", data.toString(Charsets.UTF_8))
            Logger.log("...", "[RAW]: ")
            val raw = protector.encrypt(data)
            Logger.log("...", Base64.getEncoder().encodeToString(raw))
            Logger.log("...", "-------------------------------------------------")
            sendRawBytes(raw)
        }

//        synchronized(channel) {
//            sendRawBytes(protector.encrypt(data))
//        }
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
    private fun extractLength(channel: ByteChannel): Int? {
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
     * Reads a package and returns null if the whole
     * data byte array can't be constructed yet. Otherwise
     * constructs the whole byte array from individual chunks
     * and returns it
     */
    private fun readRawChunk(): ByteArray? {
        Logger.log("...", "GETTING: new chunk")

        var length = extractLength(channel)
        Logger.log("...", "GETTING: length of chunk: $length")

        // We got data but the initial length
        // is 0
        if (length == null || length == 0)
            throw IOException("The initial length is 0 > Disconnection")

        while (length != null) {
            val parameters = extractParameters(channel)
            Logger.log("...", "GETTING: parameters: $parameters")

            val buffer = ByteBuffer.allocate(length)

            // if got something
            val bytesRead = channel.read(buffer)

            if (bytesRead <= 0)
                return null

            buffer.flip()

            // put data into a list ByteArray item
            val data = ByteArray(buffer.limit())
            var index = 0

            while (buffer.hasRemaining()) {
                data[index] = buffer.get()
                index++
            }

            Logger.log("...", "GETTING: adding chunk to cache")
            cache.add(data)

            // it was the last package
            // bring list items together into `header`
            if (!parameters.provide(PackageParameters.HAS_NEXT)) {
                Logger.log("...", "GETTING: finalizing")
                val result = cache.concat()
                cache.clear()
                return result
            }

            Logger.log("...", "GETTING: moving forward")
            length = extractLength(channel)
            Logger.log("...", "GETTING: length of chunk: $length")
        }

        // it's not the last package yet
        return null
    }

    override fun readBytes(): ByteArray? {
        synchronized(channel) {
            readRawChunk()?.let {
                Logger.log("...", "===================< RECEIVED >===================")
                Logger.log("...", "[RAW]: ")
                Logger.log("...", Base64.getEncoder().encodeToString(it))
                Logger.log("...","[DECODED]: ")
                val data = protector.decrypt(it)
                Logger.log("...", data.toString(Charsets.UTF_8))
                Logger.log("...", "--------------------------------------------------")
                return data
            }
        }

        return null
    }
}