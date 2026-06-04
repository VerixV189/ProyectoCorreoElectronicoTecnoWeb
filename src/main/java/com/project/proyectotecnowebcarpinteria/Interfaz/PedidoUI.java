package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.PedidoController;
import java.sql.Date;
import java.util.List;

/**
 * UI de correo para el recurso Pedido.
 *
 * Comandos soportados:
 *   LISPED[*]
 *   REGPED[codigo,precio,fechaEntrega,idCotizacion]    (fechaEntrega: YYYY-MM-DD)
 *   ACTPED[id,codigo,precio,fechaEntrega,idCotizacion]
 *   ELIMPED[id]
 */
public class PedidoUI implements ComandoUI {

    private final PedidoController controller;

    public PedidoUI() {
        this.controller = new PedidoController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISPED":  return listar();
                case "REGPED":  return registrar(parametros);
                case "ACTPED":  return actualizar(parametros);
                case "ELIMPED": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a PedidoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE PEDIDOS ===\n(No hay pedidos registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE PEDIDOS ===\n");
        sb.append(String.format("%-5s %-15s %-10s %-15s %-12s%n", "ID", "Código", "Precio", "Fecha Entrega", "idCotizacion"));
        sb.append("-".repeat(60)).append("\n");
        for (String[] p : lista) sb.append(String.format("%-5s %-15s %-10s %-15s %-12s%n", get(p,0), get(p,1), get(p,2), get(p,3), get(p,4)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 4, "codigo,precio,fechaEntrega(YYYY-MM-DD),idCotizacion");
        int id = controller.registrar(p[0], parseFloat(p[1], "precio"), parseDate(p[2]), parseInt(p[3], "idCotizacion"));
        return "=== PEDIDO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 5, "id,codigo,precio,fechaEntrega(YYYY-MM-DD),idCotizacion");
        controller.actualizar(parseInt(p[0], "id"), p[1], parseFloat(p[2], "precio"), parseDate(p[3]), parseInt(p[4], "idCotizacion"));
        return "=== PEDIDO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== PEDIDO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split(",", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private float parseFloat(String v, String c) { try { return Float.parseFloat(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser numérico. Recibido: \"" + v + "\""); } }
    private Date parseDate(String v) { try { return Date.valueOf(v.trim()); } catch (Exception e) { throw new IllegalArgumentException("Fecha inválida. Formato esperado: YYYY-MM-DD. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n  LISPED[*]\n  REGPED[codigo,precio,fechaEntrega(YYYY-MM-DD),idCotizacion]\n"
                + "  ACTPED[id,codigo,precio,fechaEntrega(YYYY-MM-DD),idCotizacion]\n  ELIMPED[id]";
    }
}
