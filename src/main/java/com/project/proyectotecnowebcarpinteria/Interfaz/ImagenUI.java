package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.ImagenController;
import java.util.List;

/**
 * UI de correo para el recurso Imagen.
 *
 * Comandos soportados:
 *   LISIMG[*]
 *   REGIMG[url,idProducto,idInsumo]
 *   ACTIMG[id,url,idProducto,idInsumo]
 *   ELIMIMG[id]
 */
public class ImagenUI implements ComandoUI {

    private final ImagenController controller;

    public ImagenUI() {
        this.controller = new ImagenController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISIMG":  return listar();
                case "REGIMG":  return registrar(parametros);
                case "ACTIMG":  return actualizar(parametros);
                case "ELIMIMG": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a ImagenUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE IMÁGENES ===\n(No hay imágenes registradas.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE IMÁGENES ===\n");
        sb.append(String.format("%-5s %-40s %-12s %-12s%n", "ID", "URL", "idProducto", "idInsumo"));
        sb.append("-".repeat(75)).append("\n");
        for (String[] i : lista) sb.append(String.format("%-5s %-40s %-12s %-12s%n", get(i,0), get(i,1), get(i,2), get(i,3)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 3, "url,idProducto,idInsumo");
        int id = controller.registrar(p[0], parseIntOrZero(p[1]), parseIntOrZero(p[2]));
        return "=== IMAGEN REGISTRADA ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 4, "id,url,idProducto,idInsumo");
        controller.actualizar(parseInt(p[0], "id"), p[1], parseIntOrZero(p[2]), parseIntOrZero(p[3]));
        return "=== IMAGEN ACTUALIZADA ===\nID: " + p[0] + " actualizada correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== IMAGEN ELIMINADA ===\nID: " + id + " eliminada correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private int parseIntOrZero(String v) {
        if (v == null || v.trim().isEmpty() || v.trim().equalsIgnoreCase("null") || v.trim().equals("0")) {
            return 0;
        }
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo debe ser entero, vacío o 'null'. Recibido: \"" + v + "\""); }
    }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n  LISIMG[*]\n  REGIMG[url,idProducto,idInsumo]\n  ACTIMG[id,url,idProducto,idInsumo]\n  ELIMIMG[id]";
    }
}
