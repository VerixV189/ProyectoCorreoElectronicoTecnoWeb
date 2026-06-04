package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.ProductoController;
import java.util.List;

/**
 * UI de correo para el recurso Producto.
 *
 * Comandos soportados:
 *   LISPROD[*]
 *   REGPROD[nombre,cantidad,precio,descripcion,estado,idTipo]
 *   ACTPROD[id,nombre,cantidad,precio,descripcion,estado,idTipo]
 *   ELIMPROD[id]
 */
public class ProductoUI implements ComandoUI {

    private final ProductoController controller;

    public ProductoUI() {
        this.controller = new ProductoController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISPROD":  return listar();
                case "REGPROD":  return registrar(parametros);
                case "ACTPROD":  return actualizar(parametros);
                case "ELIMPROD": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a ProductoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) {
            return "=== LISTA DE PRODUCTOS ===\n(No hay productos registrados.)";
        }
        StringBuilder sb = new StringBuilder("=== LISTA DE PRODUCTOS ===\n");
        sb.append(String.format("%-5s %-20s %-8s %-10s %-10s%n", "ID", "Nombre", "Cantidad", "Precio", "Estado"));
        sb.append("-".repeat(60)).append("\n");
        for (String[] p : lista) {
            sb.append(String.format("%-5s %-20s %-8s %-10s %-10s%n",
                    get(p, 0), get(p, 1), get(p, 2), get(p, 3), get(p, 4)));
        }
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 6, "nombre,cantidad,precio,descripcion,estado,idTipo");
        int id = controller.registrar(p[0], parseInt(p[1], "cantidad"), parseFloat(p[2], "precio"),
                p[3], p[4], parseInt(p[5], "idTipo"));
        return "=== PRODUCTO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 7, "id,nombre,cantidad,precio,descripcion,estado,idTipo");
        controller.actualizar(parseInt(p[0], "id"), p[1], parseInt(p[2], "cantidad"),
                parseFloat(p[3], "precio"), p[4], p[5], parseInt(p[6], "idTipo"));
        return "=== PRODUCTO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== PRODUCTO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
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
                + "Uso:\n  LISPROD[*]\n  REGPROD[nombre,cantidad,precio,descripcion,estado,idTipo]\n"
                + "  ACTPROD[id,nombre,cantidad,precio,descripcion,estado,idTipo]\n  ELIMPROD[id]";
    }
}
