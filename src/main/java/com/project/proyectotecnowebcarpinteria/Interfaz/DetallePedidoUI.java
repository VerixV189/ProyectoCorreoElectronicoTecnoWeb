package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.DetallePedidoController;
import java.util.List;

/**
 * UI de correo para el recurso DetallePedido.
 *
 * Comandos soportados:
 *   LISDET[idPedido]
 *   REGDET[cantidad,precio,estado,descripcion,idPedido,idProducto]
 *   ACTDET[id,cantidad,precio,estado,descripcion,idPedido,idProducto]
 *   ELIMDET[id]
 */
public class DetallePedidoUI implements ComandoUI {

    private final DetallePedidoController controller;

    public DetallePedidoUI() {
        this.controller = new DetallePedidoController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISDET":  return listar(parametros);
                case "REGDET":  return registrar(parametros);
                case "ACTDET":  return actualizar(parametros);
                case "ELIMDET": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a DetallePedidoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar(String params) {
        int idPedido = parseInt(params.trim(), "idPedido");
        List<String[]> lista = controller.listar(idPedido);
        if (lista == null || lista.isEmpty()) return "=== DETALLES DEL PEDIDO " + idPedido + " ===\n(No hay detalles registrados.)";
        StringBuilder sb = new StringBuilder("=== DETALLES DEL PEDIDO " + idPedido + " ===\n");
        sb.append(String.format("%-5s %-8s %-10s %-12s %-25s%n", "ID", "Cant.", "Precio", "Estado", "Descripción"));
        sb.append("-".repeat(65)).append("\n");
        for (String[] d : lista) sb.append(String.format("%-5s %-8s %-10s %-12s %-25s%n", get(d,0), get(d,1), get(d,2), get(d,3), get(d,4)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 6, "cantidad,precio,estado,descripcion,idPedido,idProducto");
        int id = controller.registrar(parseInt(p[0], "cantidad"), parseFloat(p[1], "precio"),
                p[2], p[3], parseInt(p[4], "idPedido"), parseInt(p[5], "idProducto"));
        return "=== DETALLE REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 7, "id,cantidad,precio,estado,descripcion,idPedido,idProducto");
        controller.actualizar(parseInt(p[0], "id"), parseInt(p[1], "cantidad"), parseFloat(p[2], "precio"),
                p[3], p[4], parseInt(p[5], "idPedido"), parseInt(p[6], "idProducto"));
        return "=== DETALLE ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== DETALLE ELIMINADO ===\nID: " + id + " eliminado correctamente.";
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
                + "Uso:\n  LISDET[idPedido]\n  REGDET[cantidad,precio,estado,descripcion,idPedido,idProducto]\n"
                + "  ACTDET[id,cantidad,precio,estado,descripcion,idPedido,idProducto]\n  ELIMDET[id]";
    }
}
