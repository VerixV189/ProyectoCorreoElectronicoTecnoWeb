package com.project.proyectotecnowebcarpinteria.Pruebas;

import java.io.*;
import java.net.*;

/**
 * Encapsula el envío de correos electrónicos mediante el protocolo SMTP.
 *
 * Basado en el ejemplo de ServidorSMTP.java.
 */
public class ClienteSMTP {

    private final String servidor;
    private final int puerto;
    private final String correoEmisor;

    /**
     * Crea una nueva instancia con los parámetros de conexión SMTP.
     *
     * @param servidor     Dirección del servidor SMTP (ej: "mail.ficct.uagrm.edu.bo")
     * @param puerto       Puerto SMTP (generalmente 25)
     * @param correoEmisor Dirección de correo del remitente
     */
    public ClienteSMTP(String servidor, int puerto, String correoEmisor) {
        this.servidor      = servidor;
        this.puerto        = puerto;
        this.correoEmisor  = correoEmisor;
    }

    /**
     * Abre una conexión SMTP, envía el correo al destinatario indicado
     * y cierra la conexión.
     *
     * El flujo de comandos es:
     * EHLO → MAIL FROM → RCPT TO → DATA → [cuerpo] → QUIT
     *
     * @param destinatario Dirección de correo del receptor (ej: "usuario@dominio.com")
     * @param asunto       Asunto del correo
     * @param cuerpo       Contenido del cuerpo del correo
     * @throws IOException Si ocurre algún error de comunicación con el servidor.
     */
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) throws IOException {
        Socket socket = new Socket(servidor, puerto);
        BufferedReader entrada   = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream salida  = new DataOutputStream(socket.getOutputStream());

        try {
            if (socket.isConnected()) {
                // Bienvenida del servidor
                System.out.println("S : " + entrada.readLine());

                // Identificación del cliente SMTP
                enviarComando(salida, "EHLO " + servidor + " \r\n");
                System.out.println("S : " + getMultiline(entrada));

                // Indicar el remitente
                enviarComando(salida, "MAIL FROM : " + correoEmisor + " \r\n");
                System.out.println("S : " + entrada.readLine());

                // Indicar el destinatario
                enviarComando(salida, "RCPT TO : " + destinatario + " \r\n");
                System.out.println("S : " + entrada.readLine());

                // Iniciar el cuerpo del mensaje
                enviarComando(salida, "DATA\r\n");
                System.out.println("S : " + entrada.readLine());

                // Enviar encabezados y cuerpo del correo
                // El punto en línea sola indica el fin del mensaje
                String mensaje = "From: " + correoEmisor + "\r\n"
                        + "To: " + destinatario + "\r\n"
                        + "Subject: " + asunto + "\r\n"
                        + "\r\n"
                        + cuerpo + "\r\n"
                        + ".\r\n";
                enviarComando(salida, mensaje);
                System.out.println("S : " + entrada.readLine());

                // Cerrar la sesión SMTP
                enviarComando(salida, "QUIT\r\n");
                System.out.println("S : " + entrada.readLine());

                System.out.println("Correo enviado correctamente a: " + destinatario);
            }
        } finally {
            salida.close();
            entrada.close();
            socket.close();
        }
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares privados
    // -------------------------------------------------------------------------

    /**
     * Envía un comando al servidor y lo imprime en consola.
     */
    private void enviarComando(DataOutputStream salida, String comando) throws IOException {
        System.out.print("C : " + comando);
        salida.writeBytes(comando);
    }

    /**
     * Lee una respuesta multi-línea del protocolo SMTP.
     * En SMTP, la última línea de una respuesta multi-línea tiene un espacio
     * en la posición 3 del código (ej: "250 OK"), mientras que las intermedias
     * tienen un guión (ej: "250-SIZE 14680064").
     *
     * @param in El lector del stream de entrada del socket.
     * @return Todas las líneas de la respuesta concatenadas.
     * @throws IOException Si el servidor cierra la conexión inesperadamente.
     */
    static protected String getMultiline(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = in.readLine();
            if (line == null) {
                throw new IOException("S : El servidor cerró la conexión inesperadamente.");
            }
            lines.append("\n").append(line);
            // La línea final de la respuesta SMTP tiene un espacio en posición 3
            if (line.length() >= 4 && line.charAt(3) == ' ') {
                break;
            }
        }
        return lines.toString();
    }
}
