package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.TipoController;
import java.util.List;

/**
 * UI de correo para el recurso Tipo.
 *
 * Comandos soportados:
 *   LISTIP[*]
 *   REGTIP[nombre,descripcion,estado]
 *   ACTTIP[id,nombre,descripcion,estado]
 *   ELIMTIP[id]
 */
public class TipoUI implements ComandoUI {

    private final TipoController controller;

    public TipoUI() {
        this.controller = new TipoController();
    }

    @Override
    public String ejecutar(String comando, String parametros, java.util.List<String> imagenesAdjuntas) {
        try {
            switch (comando) {
                case "LISTIP":  return listar();
                case "REGTIP":  return registrar(parametros);
                case "ACTTIP":  return actualizar(parametros);
                case "ELIMTIP": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a TipoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE TIPOS ===\n(No hay tipos registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE TIPOS ===\n");
        sb.append(String.format("%-5s %-20s %-30s %-10s%n", "ID", "Nombre", "Descripción", "Estado"));
        sb.append("-".repeat(68)).append("\n");
        for (String[] t : lista) sb.append(String.format("%-5s %-20s %-30s %-10s%n", get(t,0), get(t,1), get(t,2), get(t,3)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 3, "nombre,descripcion,estado");
        int id = controller.registrar(p[0], p[1], p[2]);
        return "=== TIPO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 4, "id,nombre,descripcion,estado");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2], p[3]);
        return "=== TIPO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== TIPO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
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
                + "Uso:\n  LISTIP[*]\n  REGTIP[nombre,descripcion,estado]\n"
                + "  ACTTIP[id,nombre,descripcion,estado]\n  ELIMTIP[id]";
    }
}
