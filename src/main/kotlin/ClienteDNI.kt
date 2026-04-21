import java.io.*
import java.net.Socket
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

fun main() {
    print("Operación (SUBIR/BAJAR): ")
    val operacion = readLine()!!.uppercase()

    print("ID de usuario: ")
    val usuarioId = readLine()!!.toInt()

    val socket = Socket("localhost", 9000)
    val entrada = DataInputStream(socket.getInputStream())
    val salida = DataOutputStream(socket.getOutputStream())

    salida.writeUTF(operacion)
    salida.writeInt(usuarioId)

    when (operacion) {
        "SUBIR" -> subirDNI(usuarioId, entrada, salida)
        "BAJAR" -> bajarDNI(usuarioId, entrada)
        else -> println("Operación no válida")
    }

    socket.close()
}

fun subirDNI(usuarioId: Int, entrada: DataInputStream, salida: DataOutputStream) {
    print("Ruta del archivo DNI (imagen): ")
    val ruta = readLine()!!
    val archivo = File(ruta)

    if (!archivo.exists()) {
        println("Archivo no encontrado")
        return
    }

    val bytes = archivo.readBytes()
    salida.writeInt(bytes.size)
    salida.write(bytes)

    val respuesta = entrada.readUTF()
    if (respuesta == "OK") {
        val claveBase64 = entrada.readUTF()
        // Guardar clave localmente para poder desencriptar después
        File("clave_dni_$usuarioId.key").writeText(claveBase64)
        println("DNI subido correctamente.")
        println("Clave guardada en: clave_dni_$usuarioId.key — ¡NO la pierdas!")
    } else {
        println("Error al subir: $respuesta")
    }
}

fun bajarDNI(usuarioId: Int, entrada: DataInputStream) {
    val archivoClaveStr = "clave_dni_$usuarioId.key"
    val archivoClave = File(archivoClaveStr)

    if (!archivoClave.exists()) {
        println("No se encontró la clave local ($archivoClaveStr)")
        return
    }

    val claveBase64 = archivoClave.readText()
    val claveBytes = Base64.getDecoder().decode(claveBase64)
    val clave = SecretKeySpec(claveBytes, "AES")

    val respuesta = entrada.readUTF()
    if (respuesta == "OK") {
        val tamaño = entrada.readInt()
        val bytesEncriptados = ByteArray(tamaño)
        entrada.readFully(bytesEncriptados)

        val bytesDesencriptados = CryptoUtils.desencriptar(bytesEncriptados, clave)
        val destino = "dni_descargado_$usuarioId.jpg"
        File(destino).writeBytes(bytesDesencriptados)
        println("DNI descargado y desencriptado en: $destino")
    } else {
        val msg = entrada.readUTF()
        println("Error del servidor: $msg")
    }
}