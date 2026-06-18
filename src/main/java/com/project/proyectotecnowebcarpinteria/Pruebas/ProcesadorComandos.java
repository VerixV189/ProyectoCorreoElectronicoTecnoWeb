package com.project.proyectotecnowebcarpinteria.Pruebas;

import com.project.proyectotecnowebcarpinteria.Interfaz.ComandoUI;
import com.project.proyectotecnowebcarpinteria.Interfaz.RegistroComandos;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Punto de integración entre la capa de correo (Pruebas) y la capa de
 * interfaz (Interfaz).
 *
 * Extrae el comando y sus parámetros del cuerpo de un correo electrónico,
 * consulta el {@link RegistroComandos} y delega la ejecución a la
 * {@link ComandoUI} correspondiente.
 *
 * Reglas de los comandos:
 *   - Deben escribirse en MAYÚSCULAS.
 *   - Formato: NOMBRECOMANDO[parametros]
 *   - Ejemplo: LISUSU[*]   REGUSU[Juan,Perez,juan@mail.com,pass,70000000,activo,1]
 */
public class ProcesadorComandos {

    /**
     * Regex genérico que detecta un comando del sistema.
     * Grupo 1 → nombre del comando (letras A-Z).
     * Grupo 2 → contenido dentro de los corchetes (puede estar vacío o ser "*").
     */
    private static final Pattern PATRON_COMANDO = Pattern.compile("([A-Za-z]+)\\[([^\\]]*)\\]");

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Analiza el cuerpo del correo, detecta el primer comando válido y
     * devuelve el texto de respuesta generado por la capa Interfaz.
     *
     * @param cuerpo El cuerpo del correo ya extraído (sin encabezados).
     * @return Texto de respuesta listo para enviar por correo electrónico.
     */
    public String procesarComando(String cuerpo, java.util.List<String> imagenesAdjuntas) {
        try {
            if (cuerpo == null || cuerpo.isBlank()) {
                return errorCuerpoVacio();
            }

            Matcher matcher = PATRON_COMANDO.matcher(cuerpo);

            if (!matcher.find()) {
                return errorFormatoInvalido(cuerpo);
            }

            String nombreRaw   = matcher.group(1);           // tal como vino (puede ser minúscula)
            String parametros  = matcher.group(2).trim();
            String nombreUpper = nombreRaw.toUpperCase();

            // 1. Verificar mayúsculas: si el usuario mandó en minúsculas, advertir.
            if (!nombreRaw.equals(nombreUpper)) {
                return errorMinusculas(nombreRaw, nombreUpper, parametros);
            }

            // 2. Buscar la UI en el registro.
            ComandoUI ui = RegistroComandos.obtener(nombreUpper);

            if (ui == null) {
                return errorComandoNoEncontrado(nombreUpper);
            }

            // 3. Parsear parámetros CSV (maneja comas dentro de comillas).
            String parametrosNorm = parsearCSV(parametros);

            // 4. Delegar la ejecución a la UI correspondiente.
            return ui.ejecutar(nombreUpper, parametrosNorm, imagenesAdjuntas);

        } catch (Exception e) {
            return "=== ERROR INESPERADO ===\n"
                    + "Motivo: " + e.getMessage() + "\n"
                    + "Verifique el formato del comando y vuelva a intentarlo.\n"
                    + "========================\n\n"
                    + RegistroComandos.listarComandosDisponibles();
        }
    }

    // -------------------------------------------------------------------------
    // Parser CSV de parámetros
    // -------------------------------------------------------------------------

    /**
     * Separador interno utilizado entre los tokens extraídos del CSV.
     * Se usa el carácter ASCII "Unit Separator" (\u001F), que nunca aparecerá
     * en el texto de un correo normal.
     * Las clases UI deben hacer split por este carácter en lugar de por coma.
     */
    static final String SEP = "\u001F";

    /**
     * Parsea una cadena de parámetros en formato CSV con soporte para:
     *   - Comas dentro de campos entre comillas dobles.
     *   - Comillas dobles escapadas como "" dentro de un campo.
     *   - Parámetros sin comillas (texto plano).
     *
     * Ejemplos válidos:
     *   Juan,Perez,juan@mail.com
     *   "Juan","Perez","juan@mail.com"
     *   "Mesa de madera, con acabados finos",5,"250.00"
     *
     * @param params La cadena de parámetros extraída del cuerpo del correo.
     * @return Los tokens separados por {@link #SEP}, sin comillas envolventes.
     */
    private static String parsearCSV(String params) {
        if (params == null || params.isBlank()) return params;

        StringBuilder resultado = new StringBuilder();
        StringBuilder token     = new StringBuilder();
        boolean dentroComillas  = false;

        for (int i = 0; i < params.length(); i++) {
            char c = params.charAt(i);

            if (c == '"') {
                // Comilla doble escapada: "" dentro de un campo
                if (dentroComillas && i + 1 < params.length() && params.charAt(i + 1) == '"') {
                    token.append('"');
                    i++;
                } else {
                    dentroComillas = !dentroComillas;
                }
            } else if (c == ',' && !dentroComillas) {
                // Separador de campo (fuera de comillas)
                if (resultado.length() > 0) resultado.append(SEP);
                resultado.append(token.toString().trim());
                token.setLength(0);
            } else {
                token.append(c);
            }
        }
        // Último token
        if (resultado.length() > 0) resultado.append(SEP);
        resultado.append(token.toString().trim());

        return resultado.toString();
    }

    // -------------------------------------------------------------------------
    // Utilidades de parsing de correo (estáticas, usadas por PuntoDeEntrada)
    // -------------------------------------------------------------------------

    public static String getCuerpo(String correoCompleto) {
        if (correoCompleto == null) return null;
        
        // Si es multiparte, buscar la primera parte de texto (text/plain)
        if (correoCompleto.toLowerCase().contains("content-type: multipart/")) {
            String boundary = extraerBoundary(correoCompleto);
            if (boundary != null) {
                String[] partes = correoCompleto.split("--" + boundary);
                for (String parte : partes) {
                    if (parte.toLowerCase().contains("content-type: text/plain") || 
                       (!parte.toLowerCase().contains("content-type:") && parte.trim().length() > 0 && !parte.equals("--\r\n"))) {
                        // Extraer cuerpo quitando encabezados de la parte
                        String[] subpartes = parte.split("(\\r?\\n){2}", 2);
                        if (subpartes.length == 2) {
                            return subpartes[1].trim();
                        }
                    }
                }
            }
        }

        // Correo simple
        String[] partes = correoCompleto.split("(\\r?\\n){2}", 2);
        if (partes.length == 2) {
            System.out.println("Cuerpo separado correctamente.");
            return partes[1].replaceAll("(?m)^\\.\\r?\\n$", "").trim();
        }
        System.out.println("No se pudo separar el cuerpo o el correo está vacío.");
        return null;
    }

    private static String extraerBoundary(String correo) {
        Matcher m = Pattern.compile("boundary=\"?([^\";\\s]+)\"?").matcher(correo);
        if (m.find()) return m.group(1);
        return null;
    }

    /**
     * Extrae las imágenes adjuntas en Base64 de un correo multiparte,
     * las decodifica y las guarda en el directorio local.
     */
    public static java.util.List<String> extraerImagenes(String correoCompleto) {
        java.util.List<String> archivosGuardados = new java.util.ArrayList<>();
        if (correoCompleto == null || !correoCompleto.toLowerCase().contains("content-type: multipart/")) {
            return archivosGuardados;
        }

        String boundary = extraerBoundary(correoCompleto);
        if (boundary == null) return archivosGuardados;

        java.io.File dir = new java.io.File("imagenes_adjuntas");
        if (!dir.exists()) dir.mkdirs();

        String[] partes = correoCompleto.split("--" + boundary);
        for (String parte : partes) {
            if (parte.toLowerCase().contains("content-type: image/")) {
                // Encontrar el nombre del archivo si existe
                String filename = "img_" + System.currentTimeMillis() + ".jpg";
                Matcher fm = Pattern.compile("filename=\"?([^\";\\s]+)\"?").matcher(parte);
                if (fm.find()) filename = fm.group(1);

                // Encontrar el contenido base64 (después de doble salto de línea)
                String[] subpartes = parte.split("(\\r?\\n){2}", 2);
                if (subpartes.length == 2) {
                    String base64Data = subpartes[1].replaceAll("\\s+", ""); // Quitar saltos de línea
                    try {
                        byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
                        java.io.File file = new java.io.File(dir, filename);
                        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                            fos.write(bytes);
                        }
                        archivosGuardados.add(file.getName());
                        System.out.println("Imagen guardada: " + file.getAbsolutePath());
                    } catch (Exception ex) {
                        System.out.println("Error decodificando imagen: " + ex.getMessage());
                    }
                }
            }
        }
        return archivosGuardados;
    }

    /**
     * Extrae la dirección de correo del emisor desde el encabezado {@code From:}.
     *
     * @param correoCompleto El texto completo del correo.
     * @return El email del emisor, o {@code null} si no se encontró.
     */
    public static String obtenerCorreoEmisor(String correoCompleto) {
        if (correoCompleto == null || correoCompleto.isEmpty()) return null;

        Pattern patronFrom = Pattern.compile("(?i)^From:\\s*(.*)$", Pattern.MULTILINE);
        Matcher buscadorFrom = patronFrom.matcher(correoCompleto);

        if (buscadorFrom.find()) {
            Pattern patronEmail = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher buscadorEmail = patronEmail.matcher(buscadorFrom.group(1));
            if (buscadorEmail.find()) return buscadorEmail.group();
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Mensajes de error
    // -------------------------------------------------------------------------

    private String errorCuerpoVacio() {
        return "=== ERROR ===\n"
                + "Motivo: El cuerpo del correo está vacío.\n"
                + "Por favor, incluya un comando en el cuerpo del correo.\n"
                + "=============\n\n"
                + RegistroComandos.listarComandosDisponibles();
    }

    private String errorFormatoInvalido(String cuerpo) {
        return "=== ERROR ===\n"
                + "Motivo: No se encontró ningún comando con el formato correcto.\n"
                + "Contenido recibido: \"" + cuerpo + "\"\n"
                + "Formato esperado: NOMBRECOMANDO[parametros]\n"
                + "Ejemplo: LISUSU[*]\n"
                + "=============\n\n"
                + RegistroComandos.listarComandosDisponibles();
    }

    private String errorMinusculas(String nombreRaw, String nombreUpper, String params) {
        return "=== ERROR ===\n"
                + "Comando recibido: " + nombreRaw + "[" + params + "]\n"
                + "Motivo: Los comandos deben escribirse en MAYÚSCULAS.\n"
                + "Corrección sugerida: " + nombreUpper + "[" + params + "]\n"
                + "=============\n\n"
                + RegistroComandos.listarComandosDisponibles();
    }

    private String errorComandoNoEncontrado(String nombre) {
        return "=== ERROR ===\n"
                + "Comando recibido: " + nombre + "\n"
                + "Motivo: Comando no reconocido por el sistema.\n"
                + "=============\n\n"
                + RegistroComandos.listarComandosDisponibles();
    }
}
