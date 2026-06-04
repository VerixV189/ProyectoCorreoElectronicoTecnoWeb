package com.project.proyectotecnowebcarpinteria.Pruebas;

import java.io.*;
import java.net.*;

/**
 * Encapsula la comunicación con un servidor POP3.
 * Permite conectarse, listar, leer y eliminar mensajes del buzón.
 *
 * Basado en el ejemplo de ClientePOP.java.
 */
public class ClientePOP3 {

    private final String servidor;
    private final int puerto;
    private final String usuario;
    private final String contrasena;

    private Socket socket;
    private BufferedReader entrada;
    private DataOutputStream salida;

    /**
     * Crea una nueva instancia con los parámetros de conexión.
     *
     * @param servidor  Dirección del servidor POP3 (ej: "www.tecnoweb.org.bo")
     * @param puerto    Puerto POP3 (generalmente 110)
     * @param usuario   Nombre de usuario del buzón
     * @param contrasena Contraseña del buzón
     */
    public ClientePOP3(String servidor, int puerto, String usuario, String contrasena) {
        this.servidor  = servidor;
        this.puerto    = puerto;
        this.usuario   = usuario;
        this.contrasena = contrasena;
    }

    /**
     * Abre la conexión TCP con el servidor y realiza el login POP3
     * (USER + PASS). Debe llamarse antes de cualquier otro método.
     *
     * @throws IOException Si no se puede conectar o las credenciales son inválidas.
     */
    public void conectar() throws IOException {
        socket  = new Socket(servidor, puerto);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        salida  = new DataOutputStream(socket.getOutputStream());

        // Bienvenida del servidor
        System.out.println("S : " + entrada.readLine());

        // Autenticación
        enviarComando("USER " + usuario + "\r\n");
        System.out.println("S : " + entrada.readLine());

        enviarComando("PASS " + contrasena + "\r\n");
        System.out.println("S : " + entrada.readLine());
    }

    /**
     * Ejecuta el comando LIST y devuelve el número total de mensajes
     * disponibles en el buzón. Devuelve 0 si el buzón está vacío.
     *
     * @return Número de mensajes en el buzón.
     * @throws IOException Si ocurre un error de comunicación.
     */
    public int contarMensajes() throws IOException {
        enviarComando("LIST \r\n");
        String respuesta = getMultiline(entrada);
        System.out.println("S : " + respuesta);

        // La primera línea es "+OK N messages (M octets)"
        String primeraLinea = respuesta.trim().split("\n")[0];
        try {
            // Extrae el número total de mensajes del inicio de la respuesta
            String[] partes = primeraLinea.trim().split("\\s+");
            if (partes.length >= 2) {
                return Integer.parseInt(partes[1]);
            }
        } catch (NumberFormatException e) {
            System.out.println("No se pudo parsear el número de mensajes.");
        }
        return 0;
    }

    /**
     * Descarga el contenido completo (encabezados + cuerpo) de un mensaje.
     *
     * @param numero Número del mensaje a recuperar (empieza en 1).
     * @return El texto completo del correo electrónico.
     * @throws IOException Si ocurre un error de comunicación.
     */
    public String leerMensaje(int numero) throws IOException {
        enviarComando("RETR " + numero + "\r\n");
        String contenido = getMultiline(entrada);
        System.out.println("S : [RETR " + numero + "] mensaje recuperado.");
        return contenido;
    }

    /**
     * Marca un mensaje para ser eliminado del servidor.
     * La eliminación efectiva ocurre al llamar a {@link #cerrarSesion()}.
     *
     * @param numero Número del mensaje a eliminar.
     * @throws IOException Si ocurre un error de comunicación.
     */
    public void eliminarMensaje(int numero) throws IOException {
        enviarComando("DELE " + numero + "\r\n");
        System.out.println("S : " + entrada.readLine());
    }

    /**
     * Cierra la sesión POP3 enviando QUIT y libera los recursos de red.
     * Es aquí donde el servidor borra permanentemente los mensajes marcados.
     *
     * @throws IOException Si ocurre un error al cerrar.
     */
    public void cerrarSesion() throws IOException {
        enviarComando("QUIT\r\n");
        System.out.println("S : " + entrada.readLine());

        salida.close();
        entrada.close();
        socket.close();
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares privados
    // -------------------------------------------------------------------------

    /**
     * Envía un comando al servidor y lo imprime en consola.
     */
    private void enviarComando(String comando) throws IOException {
        System.out.print("C : " + comando);
        salida.writeBytes(comando);
    }

    /**
     * Lee una respuesta multi-línea del protocolo POP3.
     * La respuesta termina cuando el servidor envía una línea con solo ".".
     *
     * @param in El lector del stream de entrada del socket.
     * @return Todas las líneas concatenadas.
     * @throws IOException Si el servidor cierra la conexión inesperadamente.
     */
    static protected String getMultiline(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = in.readLine();
            if (line == null) {
                throw new IOException("S : El servidor cerró la conexión inesperadamente.");
            }
            // Línea de fin de respuesta multi-línea POP3
            if (line.equals(".")) {
                break;
            }
            // El protocolo POP3 escapa los puntos iniciales duplicándolos
            if (line.length() > 0 && line.charAt(0) == '.') {
                line = line.substring(1);
            }
            lines.append("\n").append(line);
        }
        return lines.toString();
    }
}
