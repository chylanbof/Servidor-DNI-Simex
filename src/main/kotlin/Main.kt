import java.net.ServerSocket

fun main() {
    val puerto = 9000
    val servidor = ServerSocket(puerto)
    println("Servidor DNI escuchando en puerto $puerto...")

    // Esto es un bucle infinito que acepta clientes y lanza un thread por cada uno
    while (true) {
        val clienteSocket = servidor.accept()
        val hilo = Thread(ClientHandler(clienteSocket))
        hilo.start()
    }
}