package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.PermisoController;
import java.util.List;

/**
 * UI de correo para el recurso Permiso.
 *
 * Comandos soportados:
 *   LISPERM[*]
 *   REGPERM[nombre]
 *   ACTPERM[id,nombre]
 *   ELIMPERM[id]
 */
public class PermisoUI implements ComandoUI {

    private final PermisoController controller;

    public PermisoUI() {
        this.controller = new PermisoController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISPERM":  return listar();
                case "REGPERM":  return registrar(parametros);
                case "ACTPERM":  return actualizar(parametros);
                case "ELIMPERM": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a PermisoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE PERMISOS ===\n(No hay permisos registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE PERMISOS ===\n");
        sb.append(String.format("%-5s %-25s%n", "ID", "Nombre"));
        sb.append("-".repeat(32)).append("\n");
        for (String[] p : lista) sb.append(String.format("%-5s %-25s%n", get(p,0), get(p,1)));
        return sb.toString();
    }

    private String registrar(String params) {
        int id = controller.registrar(params.trim());
        return "=== PERMISO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 2, "id,nombre");
        controller.actualizar(parseInt(p[0], "id"), p[1]);
        return "=== PERMISO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== PERMISO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split(",", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n  LISPERM[*]\n  REGPERM[nombre]\n  ACTPERM[id,nombre]\n  ELIMPERM[id]";
    }
}
