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
    public String procesarComando(String cuerpo) {
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
            return ui.ejecutar(nombreUpper, parametrosNorm);

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

    /**
     * Extrae el cuerpo de un correo completo (encabezados + cuerpo).
     * El cuerpo comienza después de la primera línea en blanco.
     *
     * @param correoCompleto Texto completo del correo (respuesta de RETR).
     * @return El cuerpo del correo, o {@code null} si no se pudo separar.
     */
    public static String getCuerpo(String correoCompleto) {
        String[] partes = correoCompleto.split("(\\r?\\n){2}", 2);
        if (partes.length == 2) {
            System.out.println("Cuerpo separado correctamente.");
            return partes[1].replaceAll("(?m)^\\.\\r?\\n$", "").trim();
        }
        System.out.println("No se pudo separar el cuerpo o el correo está vacío.");
        return null;
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
