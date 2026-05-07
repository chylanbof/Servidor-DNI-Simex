import java.io.*
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Paths

class ClientHandler(private val socket: Socket) : Runnable {

    private val carpetaDNI = "dni_files"

    override fun run() {
        println("Cliente conectado: ${socket.inetAddress}")
        try {
            val entrada = DataInputStream(socket.getInputStream())
            val salida = DataOutputStream(socket.getOutputStream())

            val operacion = entrada.readUTF()
            val usuarioId = entrada.readInt()

            when (operacion) {
                "SUBIR" -> manejarSubida(usuarioId, entrada, salida)
                "BAJAR" -> manejarBajada(usuarioId, salida)
                else -> {
                    salida.writeUTF("ERROR: Operación desconocida")
                    salida.flush()
                }
            }

            socket.getOutputStream().flush()
            Thread.sleep(200)

        } catch (e: Exception) {
            println("Error con cliente: ${e.message}")
        } finally {
            try { socket.close() } catch (_: Exception) {}
            println("Cliente desconectado")
        }
    }

    private fun manejarSubida(usuarioId: Int, entrada: DataInputStream, salida: DataOutputStream) {
        val tamaño = entrada.readInt()
        val bytesEncriptados = ByteArray(tamaño)
        entrada.readFully(bytesEncriptados)

        println("RECIBIDO: $tamaño bytes, primeros 16: ${bytesEncriptados.take(16).map { it.toInt() and 0xFF }}")

        Files.createDirectories(Paths.get(carpetaDNI))
        val archivo = File("$carpetaDNI/dni_$usuarioId.enc")
        archivo.writeBytes(bytesEncriptados)

        // Releer el archivo y comparar
        val leido = archivo.readBytes()
        println("GUARDADO: ${leido.size} bytes, primeros 16: ${leido.take(16).map { it.toInt() and 0xFF }}")

        salida.writeUTF("OK")
        salida.flush()
    }

    private fun manejarBajada(usuarioId: Int, salida: DataOutputStream) {
        val archivo = File("$carpetaDNI/dni_$usuarioId.enc")
        if (!archivo.exists()) {
            salida.writeUTF("ERROR")
            salida.writeUTF("No se encontró el DNI")
            salida.flush()
            return
        }

        val bytes = archivo.readBytes()
        println("ENVIANDO: ${bytes.size} bytes, primeros 16: ${bytes.take(16).map { it.toInt() and 0xFF }}")

        salida.writeUTF("OK")
        salida.writeInt(bytes.size)
        salida.write(bytes)
        salida.flush()
    }
}