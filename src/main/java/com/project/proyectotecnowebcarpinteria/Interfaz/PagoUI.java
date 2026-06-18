package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.PagoController;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * UI de correo para el recurso Pago.
 *
 * Comandos soportados:
 *   LISPAG[*]
 *       → Lista todos los pagos registrados.
 *         Muestra automáticamente si un pago está EN MORA (estado 'Pendiente'
 *         y fecha_vencimiento anterior a hoy), junto con los días de retraso.
 *
 *   REGPAG[subtotal,interes,estado,fechaVencimiento(YYYY-MM-DD),idVenta]
 *       → Registra un pago manualmente.
 *         Usar estado 'Pendiente' para pagos a crédito o 'Pagado' para pagos inmediatos.
 *         Si no hay fecha de vencimiento, enviar 'null' en ese campo.
 *
 *   ACTPAG[id,subtotal,interes,estado,fechaVencimiento(YYYY-MM-DD),idVenta]
 *       → Actualiza los datos de un pago existente.
 *
 *   ELIMPAG[id]
 *       → Elimina un pago por su ID.
 *
 * NOTA: Para crear pagos automáticamente al registrar una venta,
 *       use REGVENCON (contado) o REGVENCRE (crédito).
 */
public class PagoUI implements ComandoUI {

    private final PagoController controller;

    public PagoUI() {
        this.controller = new PagoController();
    }

    @Override
    public String ejecutar(String comando, String parametros, java.util.List<String> imagenesAdjuntas) {
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

        LocalDate hoy = LocalDate.now();
        StringBuilder sb = new StringBuilder("=== LISTA DE PAGOS ===\n");
        sb.append(String.format("%-5s %-12s %-10s %-15s %-18s %-8s%n",
                "ID", "Subtotal", "Interes", "Estado", "Vencimiento", "idVenta"));
        sb.append("-".repeat(75)).append("\n");

        boolean hayMoras = false;
        StringBuilder moras = new StringBuilder();

        for (String[] p : lista) {
            String estadoDisplay = get(p, 3);
            String fechaVenc = get(p, 4);

            // Calcular mora: si está Pendiente y la fecha de vencimiento ya pasó
            if ("Pendiente".equalsIgnoreCase(get(p, 3)) && !"-".equals(fechaVenc) && fechaVenc != null && !fechaVenc.isEmpty()) {
                try {
                    LocalDate vencimiento = LocalDate.parse(fechaVenc);
                    if (vencimiento.isBefore(hoy)) {
                        long diasMora = ChronoUnit.DAYS.between(vencimiento, hoy);
                        estadoDisplay = "EN MORA (" + diasMora + " dias)";
                        hayMoras = true;
                        moras.append("  - Pago #").append(get(p, 0))
                             .append(": ").append(diasMora).append(" dias de retraso")
                             .append(" (vencio: ").append(fechaVenc).append(")\n");
                    }
                } catch (Exception ignored) { }
            }

            sb.append(String.format("%-5s %-12s %-10s %-15s %-18s %-8s%n",
                    get(p, 0), get(p, 1), get(p, 2), estadoDisplay, fechaVenc, get(p, 5)));
        }

        if (hayMoras) {
            sb.append("\n*** RESUMEN DE PAGOS EN MORA ***\n");
            sb.append(moras);
        }

        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 5, "subtotal,interes,estado,fechaVencimiento(YYYY-MM-DD o null),idVenta");
        Date fechaVenc = parseDateOrNull(p[3]);
        int id = controller.registrar(parseFloat(p[0], "subtotal"), parseFloat(p[1], "interes"), p[2], fechaVenc, parseInt(p[4], "idVenta"));
        return "=== PAGO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 6, "id,subtotal,interes,estado,fechaVencimiento(YYYY-MM-DD o null),idVenta");
        Date fechaVenc = parseDateOrNull(p[4]);
        controller.actualizar(parseInt(p[0], "id"), parseFloat(p[1], "subtotal"), parseFloat(p[2], "interes"), p[3], fechaVenc, parseInt(p[5], "idVenta"));
        return "=== PAGO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== PAGO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parametros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }

    /** Acepta una fecha en formato YYYY-MM-DD o 'null' para indicar sin fecha de vencimiento. */
    private Date parseDateOrNull(String v) {
        String trimmed = v.trim();
        if (trimmed.equalsIgnoreCase("null") || trimmed.isEmpty()) return null;
        try { return Date.valueOf(trimmed); }
        catch (Exception e) { throw new IllegalArgumentException("Fecha de vencimiento invalida. Use YYYY-MM-DD o 'null'. Recibido: \"" + trimmed + "\""); }
    }

    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private float parseFloat(String v, String c) { try { return Float.parseFloat(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser numerico. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }

    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n"
                + "  LISPAG[*]\n"
                + "  REGPAG[subtotal,interes,estado,fechaVencimiento(YYYY-MM-DD o null),idVenta]\n"
                + "  ACTPAG[id,subtotal,interes,estado,fechaVencimiento(YYYY-MM-DD o null),idVenta]\n"
                + "  ELIMPAG[id]\n"
                + "\nNOTA: Para pagos automaticos use REGVENCON o REGVENCRE.";
    }
}
