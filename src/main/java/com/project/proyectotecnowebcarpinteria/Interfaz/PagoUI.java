package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.PagoController;
import java.util.List;

/**
 * UI de correo para el recurso Pago.
 *
 * Comandos soportados:
 *   LISPAG[*]
 *   REGPAG[subtotal,interes,idVenta]
 *   ACTPAG[id,subtotal,interes,idVenta]
 *   ELIMPAG[id]
 */
public class PagoUI implements ComandoUI {

    private final PagoController controller;

    public PagoUI() {
        this.controller = new PagoController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISPAG":  return listar();
                case "REGPAG":  return registrar(parametros);
                case "ACTPAG":  return actualizar(parametros);
                case "ELIMPAG": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a PagoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE PAGOS ===\n(No hay pagos registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE PAGOS ===\n");
        sb.append(String.format("%-5s %-12s %-10s %-10s%n", "ID", "Subtotal", "Interés", "idVenta"));
        sb.append("-".repeat(40)).append("\n");
        for (String[] p : lista) sb.append(String.format("%-5s %-12s %-10s %-10s%n", get(p,0), get(p,1), get(p,2), get(p,3)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 3, "subtotal,interes,idVenta");
        int id = controller.registrar(parseFloat(p[0], "subtotal"), parseFloat(p[1], "interes"), parseInt(p[2], "idVenta"));
        return "=== PAGO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 4, "id,subtotal,interes,idVenta");
        controller.actualizar(parseInt(p[0], "id"), parseFloat(p[1], "subtotal"), parseFloat(p[2], "interes"), parseInt(p[3], "idVenta"));
        return "=== PAGO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== PAGO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split(",", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private float parseFloat(String v, String c) { try { return Float.parseFloat(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser numérico. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n  LISPAG[*]\n  REGPAG[subtotal,interes,idVenta]\n"
                + "  ACTPAG[id,subtotal,interes,idVenta]\n  ELIMPAG[id]";
    }
}
