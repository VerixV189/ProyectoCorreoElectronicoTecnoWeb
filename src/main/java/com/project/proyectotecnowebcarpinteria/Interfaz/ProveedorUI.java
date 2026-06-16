package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.ProveedorController;
import java.util.List;

/**
 * UI de correo para el recurso Proveedor.
 *
 * Comandos soportados:
 *   LISPROV[*]
 *   REGPROV[nombreEmpresa,telefono,direccion]
 *   ACTPROV[id,nombreEmpresa,telefono,direccion]
 *   ELIMPROV[id]
 */
public class ProveedorUI implements ComandoUI {

    private final ProveedorController controller;

    public ProveedorUI() {
        this.controller = new ProveedorController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISPROV":  return listar();
                case "REGPROV":  return registrar(parametros);
                case "ACTPROV":  return actualizar(parametros);
                case "ELIMPROV": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a ProveedorUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE PROVEEDORES ===\n(No hay proveedores registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE PROVEEDORES ===\n");
        sb.append(String.format("%-5s %-25s %-15s %-25s%n", "ID", "Empresa", "Teléfono", "Dirección"));
        sb.append("-".repeat(75)).append("\n");
        for (String[] p : lista) sb.append(String.format("%-5s %-25s %-15s %-25s%n", get(p,0), get(p,1), get(p,2), get(p,3)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 3, "nombreEmpresa,telefono,direccion");
        int id = controller.registrar(p[0], p[1], p[2]);
        return "=== PROVEEDOR REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 4, "id,nombreEmpresa,telefono,direccion");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2], p[3]);
        return "=== PROVEEDOR ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== PROVEEDOR ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n  LISPROV[*]\n  REGPROV[nombreEmpresa,telefono,direccion]\n"
                + "  ACTPROV[id,nombreEmpresa,telefono,direccion]\n  ELIMPROV[id]";
    }
}
