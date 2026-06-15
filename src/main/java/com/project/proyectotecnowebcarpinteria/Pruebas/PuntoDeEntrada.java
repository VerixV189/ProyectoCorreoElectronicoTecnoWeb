package com.project.proyectotecnowebcarpinteria.Pruebas;

import java.io.IOException;

/**
 * Punto de entrada del sistema de comunicación por correo electrónico.
 *
 * Orquesta el ciclo completo:
 *   1. Conectarse al servidor POP3 y leer los correos entrantes.
 *   2. Para cada correo, extraer el emisor y el cuerpo del mensaje.
 *   3. Interpretar el comando contenido en el cuerpo.
 *   4. Enviar la respuesta al emisor por SMTP.
 *   5. Eliminar el correo procesado del servidor.
 *
 * Clases de apoyo:
 *   - {@link ClientePOP3}        — lectura de correos via POP3
 *   - {@link ClienteSMTP}        — envío de correos via SMTP
 *   - {@link ProcesadorComandos} — interpretación de comandos y generación de respuestas
 */
public class PuntoDeEntrada {

    // -------------------------------------------------------------------------
    // Configuración POP3 (recepción)
    // -------------------------------------------------------------------------
    private static final String POP3_SERVIDOR   = "www.tecnoweb.org.bo";
    private static final int    POP3_PUERTO     = 110;
    private static final String POP3_USUARIO    = "grupo07sa";
    private static final String POP3_CONTRASENA = "grup007grup007*";

    // -------------------------------------------------------------------------
    // Configuración SMTP (envío)
    // -------------------------------------------------------------------------
    private static final String SMTP_SERVIDOR   = "mail.tecnoweb.org.bo";
    private static final int    SMTP_PUERTO     = 25;
    private static final String SMTP_EMISOR     = "grupo07sa@tecnoweb.org.bo";

    // -------------------------------------------------------------------------
    // Configuración del bucle de polling
    // -------------------------------------------------------------------------
    /** Tiempo de espera entre cada revisión del buzón (en segundos). */
    private static final int INTERVALO_SEGUNDOS = 10;

    // -------------------------------------------------------------------------
    // Orquestación principal
    // -------------------------------------------------------------------------

    /**
     * Ejecuta un ciclo completo de lectura, procesamiento y respuesta.
     * Procesa todos los mensajes pendientes en el buzón en este momento.
     */
    public void ejecutarCiclo() {
        ClientePOP3 pop3 = new ClientePOP3(
                POP3_SERVIDOR, POP3_PUERTO, POP3_USUARIO, POP3_CONTRASENA);
        ClienteSMTP smtp = new ClienteSMTP(
                SMTP_SERVIDOR, SMTP_PUERTO, SMTP_EMISOR);
        ProcesadorComandos procesador = new ProcesadorComandos();

        try {
            // 1. Conectar al servidor POP3
            System.out.println("=== Conectando al servidor POP3... ===");
            pop3.conectar();

            // 2. Obtener la cantidad de mensajes en el buzón
            int totalMensajes = pop3.contarMensajes();
            System.out.println("=== Mensajes en el buzón: " + totalMensajes + " ===");

            // 3. Procesar cada mensaje
            for (int i = 1; i <= totalMensajes; i++) {
                System.out.println("\n--- Procesando mensaje " + i + " de " + totalMensajes + " ---");

                // 3a. Descargar el correo completo
                String correoCompleto = pop3.leerMensaje(i);

                // 3b. Extraer el emisor del correo
                String emisor = ProcesadorComandos.obtenerCorreoEmisor(correoCompleto);
                System.out.println("Emisor detectado: " + emisor);

                // 3c. Extraer el cuerpo del correo
                String cuerpo = ProcesadorComandos.getCuerpo(correoCompleto);
                System.out.println("Cuerpo del correo:\n" + cuerpo);

                // 3d. Interpretar el comando y generar la respuesta
                String respuesta = procesador.procesarComando(cuerpo);
                System.out.println("Respuesta generada:\n" + respuesta);

                // 3e. Enviar la respuesta al emisor (si se pudo identificar)
                if (emisor != null && !emisor.isBlank()) {
                    System.out.println("=== Enviando respuesta a: " + emisor + " ===");
                    try {
                        smtp.enviarCorreo("grupo07sa@tecnoweb.org.bo", "RE: Respuesta del sistema de carpintería", respuesta);
                    } catch (Exception smtpEx) {
                        System.err.println("⚠ No se pudo enviar el correo de respuesta: " + smtpEx.getMessage());
                        System.err.println("  La respuesta que se intentó enviar fue:\n" + respuesta);
                    }
                } else {
                    System.out.println("No se pudo identificar el emisor. Se omite el envío de respuesta.");
                }

                // 3f. Marcar el correo para eliminar una vez procesado
                pop3.eliminarMensaje(i);
            }

            // 4. Cerrar la sesión POP3 (aquí se borran definitivamente los mensajes marcados)
            pop3.cerrarSesion();
            System.out.println("\n=== Ciclo completado. Sesión POP3 cerrada. ===");

        } catch (IOException e) {
            System.err.println("Error de comunicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        PuntoDeEntrada sistema = new PuntoDeEntrada();

        System.out.println("=== Sistema de carpintería iniciado. ===");
        System.out.println("=== Revisando el buzón cada " + INTERVALO_SEGUNDOS + " segundos. ===");
        System.out.println("=== Presione Ctrl+C para detener el sistema. ===");

        while (true) {
            sistema.ejecutarCiclo();

            try {
                System.out.println("\n=== Esperando " + INTERVALO_SEGUNDOS + " segundos para la próxima revisión... ===");
                Thread.sleep(INTERVALO_SEGUNDOS * 1000L);
            } catch (InterruptedException e) {
                System.out.println("Sistema interrumpido. Cerrando...");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}