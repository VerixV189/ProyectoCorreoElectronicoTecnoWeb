package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.RolController;
import java.util.List;

/**
 * UI de correo para el recurso Rol.
 *
 * Comandos soportados:
 *   LISROL[*]
 *   REGROL[nombre,estado]
 *   ACTROL[id,nombre,estado]
 *   ELIMROL[id]
 */
public class RolUI implements ComandoUI {

    private final RolController controller;

    public RolUI() {
        this.controller = new RolController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISROL":  return listar();
                case "REGROL":  return registrar(parametros);
                case "ACTROL":  return actualizar(parametros);
                case "ELIMROL": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a RolUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE ROLES ===\n(No hay roles registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE ROLES ===\n");
        sb.append(String.format("%-5s %-20s %-10s%n", "ID", "Nombre", "Estado"));
        sb.append("-".repeat(38)).append("\n");
        for (String[] r : lista) sb.append(String.format("%-5s %-20s %-10s%n", get(r,0), get(r,1), get(r,2)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 2, "nombre,estado");
        int id = controller.registrar(p[0], p[1]);
        return "=== ROL REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 3, "id,nombre,estado");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2]);
        return "=== ROL ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== ROL ELIMINADO ===\nID: " + id + " eliminado correctamente.";
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
                + "Uso:\n  LISROL[*]\n  REGROL[nombre,estado]\n  ACTROL[id,nombre,estado]\n  ELIMROL[id]";
    }
}
