package com.project.proyectotecnowebcarpinteria.Interfaz;

/**
 * Contrato que toda clase de la capa Interfaz debe implementar.
 *
 * Cada implementación corresponde a un recurso del sistema (Usuario, Cliente, etc.)
 * y sabe cómo interpretar los comandos que le corresponden, delegar al controlador
 * y formatear la respuesta como texto para enviar por correo electrónico.
 */
public interface ComandoUI {

    /**
     * Ejecuta el comando indicado con los parámetros dados y devuelve
     * el texto de respuesta que se enviará por correo al remitente.
     *
     * @param comando    Nombre del comando en MAYÚSCULAS (ej: "LISUSU", "REGUSU").
     * @param parametros Contenido dentro de los corchetes (ej: "*", "Juan,Perez,...").
     *                   Puede estar vacío si el comando no requiere parámetros.
     * @return Texto de respuesta formateado, listo para enviar como cuerpo de correo.
     */
    String ejecutar(String comando, String parametros);
}
