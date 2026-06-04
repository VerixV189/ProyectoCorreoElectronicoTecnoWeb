package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.InsumoController;
import java.util.List;

/**
 * UI de correo para el recurso Insumo.
 *
 * Comandos soportados:
 *   LISINSU[*]
 *   REGINSU[nombre,imagen,idProveedor]
 *   ACTINSU[id,nombre,imagen,idProveedor]
 *   ELIMINSU[id]
 */
public class InsumoUI implements ComandoUI {

    private final InsumoController controller;

    public InsumoUI() {
        this.controller = new InsumoController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISINSU":  return listar();
                case "REGINSU":  return registrar(parametros);
                case "ACTINSU":  return actualizar(parametros);
                case "ELIMINSU": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a InsumoUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE INSUMOS ===\n(No hay insumos registrados.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE INSUMOS ===\n");
        sb.append(String.format("%-5s %-20s %-15s%n", "ID", "Nombre", "idProveedor"));
        sb.append("-".repeat(45)).append("\n");
        for (String[] i : lista) sb.append(String.format("%-5s %-20s %-15s%n", get(i,0), get(i,1), get(i,2)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 3, "nombre,imagen,idProveedor");
        int id = controller.registrar(p[0], p[1], parseInt(p[2], "idProveedor"));
        return "=== INSUMO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 4, "id,nombre,imagen,idProveedor");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2], parseInt(p[3], "idProveedor"));
        return "=== INSUMO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== INSUMO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
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
                + "Uso:\n  LISINSU[*]\n  REGINSU[nombre,imagen,idProveedor]\n"
                + "  ACTINSU[id,nombre,imagen,idProveedor]\n  ELIMINSU[id]";
    }
}
