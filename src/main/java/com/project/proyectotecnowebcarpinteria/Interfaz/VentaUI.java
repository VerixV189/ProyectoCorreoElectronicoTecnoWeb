package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.VentaController;
import java.sql.Date;
import java.util.List;

/**
 * UI de correo para el recurso Venta.
 *
 * Comandos soportados:
 *   LISVEN[*]
 *   REGVEN[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]
 *   ACTVEN[id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]
 *   ELIMVEN[id]
 */
public class VentaUI implements ComandoUI {

    private final VentaController controller;

    public VentaUI() {
        this.controller = new VentaController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISVEN":  return listar();
                case "REGVEN":  return registrar(parametros);
                case "ACTVEN":  return actualizar(parametros);
                case "ELIMVEN": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a VentaUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE VENTAS ===\n(No hay ventas registradas.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE VENTAS ===\n");
        sb.append(String.format("%-5s %-12s %-12s %-15s %-10s%n", "ID", "Código", "Total", "Fecha", "Tipo"));
        sb.append("-".repeat(60)).append("\n");
        for (String[] v : lista) sb.append(String.format("%-5s %-12s %-12s %-15s %-10s%n", get(v,0), get(v,1), get(v,2), get(v,3), get(v,4)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 6, "codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente");
        int id = controller.registrar(p[0], parseFloat(p[1], "totalCosto"), parseDate(p[2]), p[3],
                parseInt(p[4], "idPedido"), parseInt(p[5], "idCliente"));
        return "=== VENTA REGISTRADA ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 7, "id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente");
        controller.actualizar(parseInt(p[0], "id"), p[1], parseFloat(p[2], "totalCosto"), parseDate(p[3]),
                p[4], parseInt(p[5], "idPedido"), parseInt(p[6], "idCliente"));
        return "=== VENTA ACTUALIZADA ===\nID: " + p[0] + " actualizada correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== VENTA ELIMINADA ===\nID: " + id + " eliminada correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split(",", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private float parseFloat(String v, String c) { try { return Float.parseFloat(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser numérico. Recibido: \"" + v + "\""); } }
    private Date parseDate(String v) { try { return Date.valueOf(v.trim()); } catch (Exception e) { throw new IllegalArgumentException("Fecha inválida. Formato: YYYY-MM-DD. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n  LISVEN[*]\n  REGVEN[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]\n"
                + "  ACTVEN[id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]\n  ELIMVEN[id]";
    }
}
