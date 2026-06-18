package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.DetalleCotizacionController;
import java.util.List;

/**
 * UI de correo para el recurso DetalleCotizacion.
 * Permite gestionar los detalles (precios y carpinteros) de una cotización.
 *
 * Comandos soportados:
 *   LISDETCOT[idCotizacion]
 *       → Lista todos los detalles de la cotización indicada.
 *
 *   REGDETCOT[precio,descripcion,idCotizacion,idCarpintero]
 *       → Registra un nuevo detalle en una cotización existente.
 *
 *   ACTDETCOT[id,precio,descripcion,idCotizacion,idCarpintero]
 *       → Actualiza un detalle de cotización existente.
 *
 *   ELIMDETCOT[id]
 *       → Elimina un detalle de cotización por su ID.
 */
public class DetalleCotizacionUI implements ComandoUI {

    private final DetalleCotizacionController controller;

    public DetalleCotizacionUI() {
        this.controller = new DetalleCotizacionController();
    }

    @Override
    public String ejecutar(String comando, String parametros, java.util.List<String> imagenesAdjuntas) {
        try {
            switch (comando) {
                case "LISDETCOT":  return listar(parametros);
                case "REGDETCOT":  return registrar(parametros);
                case "ACTDETCOT":  return actualizar(parametros);
                case "ELIMDETCOT": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a DetalleCotizacionUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------

    private String listar(String params) {
        int idCotizacion = parseInt(params.trim(), "idCotizacion");
        List<String[]> lista = controller.listar(idCotizacion);
        if (lista == null || lista.isEmpty()) {
            return "=== DETALLES DE COTIZACIÓN #" + idCotizacion + " ===\n"
                    + "(No hay detalles registrados para esta cotización.)";
        }
        StringBuilder sb = new StringBuilder("=== DETALLES DE COTIZACIÓN #" + idCotizacion + " ===\n");
        sb.append(String.format("%-5s %-12s %-30s %-10s %-25s%n",
                "ID", "Precio", "Descripción", "idCarp.", "Carpintero"));
        sb.append("-".repeat(90)).append("\n");
        for (String[] d : lista) {
            sb.append(String.format("%-5s %-12s %-30s %-10s %-25s%n",
                    get(d, 0), get(d, 1), get(d, 2), get(d, 4), get(d, 5)));
        }
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 4, "precio,descripcion,idCotizacion,idCarpintero");
        int id = controller.registrar(parseFloat(p[0], "precio"), p[1],
                parseInt(p[2], "idCotizacion"), parseInt(p[3], "idCarpintero"));
        return "=== DETALLE DE COTIZACIÓN REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 5, "id,precio,descripcion,idCotizacion,idCarpintero");
        controller.actualizar(parseInt(p[0], "id"), parseFloat(p[1], "precio"), p[2],
                parseInt(p[3], "idCotizacion"), parseInt(p[4], "idCarpintero"));
        return "=== DETALLE DE COTIZACIÓN ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== DETALLE DE COTIZACIÓN ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) {
            throw new IllegalArgumentException(
                    "Se esperaban " + expected + " parámetros: " + formato
                    + "\nRecibidos: " + p.length);
        }
        return p;
    }

    private int parseInt(String v, String c) {
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\"");
        }
    }

    private float parseFloat(String v, String c) {
        try { return Float.parseFloat(v.trim()); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + c + "' debe ser numérico. Recibido: \"" + v + "\"");
        }
    }

    private String get(String[] arr, int i) {
        return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-";
    }

    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n"
                + "  LISDETCOT[idCotizacion]\n"
                + "  REGDETCOT[precio,descripcion,idCotizacion,idCarpintero]\n"
                + "  ACTDETCOT[id,precio,descripcion,idCotizacion,idCarpintero]\n"
                + "  ELIMDETCOT[id]";
    }
}
