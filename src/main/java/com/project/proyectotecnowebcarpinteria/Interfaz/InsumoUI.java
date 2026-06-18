package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.InsumoController;
import java.util.List;

/**
 * UI de correo para el recurso Insumo.
 *
 * Comandos soportados:
 *   LISINSU[*]
 *   REGINSU[nombre,idProveedor]
 *   ACTINSU[id,nombre,idProveedor]
 *   ELIMINSU[id]
 */
public class InsumoUI implements ComandoUI {

    private final InsumoController controller;

    public InsumoUI() {
        this.controller = new InsumoController();
    }

    @Override
    public String ejecutar(String comando, String parametros, java.util.List<String> imagenesAdjuntas) {
        try {
            switch (comando) {
                case "LISINSU":  return listar();
                case "REGINSU":  return registrar(parametros, imagenesAdjuntas);
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
        sb.append(String.format("%-5s %-20s %-15s %-30s%n", "ID", "Nombre", "idProveedor", "Imágenes"));
        sb.append("-".repeat(75)).append("\n");
        for (String[] i : lista) sb.append(String.format("%-5s %-20s %-15s %-30s%n", get(i,0), get(i,1), get(i,2), get(i,5)));
        return sb.toString();
    }

    private String registrar(String params, List<String> imagenesAdjuntas) {
        String[] p = split(params, 2, "nombre,idProveedor");
        int id = controller.registrar(p[0], parseInt(p[1], "idProveedor"));
        
        int imgsRegistradas = 0;
        if (id > 0 && imagenesAdjuntas != null && !imagenesAdjuntas.isEmpty()) {
            com.project.proyectotecnowebcarpinteria.Controlador.ImagenController imgCtrl = new com.project.proyectotecnowebcarpinteria.Controlador.ImagenController();
            for (String imgUrl : imagenesAdjuntas) {
                imgCtrl.registrar(imgUrl, 0, id);
                imgsRegistradas++;
            }
        }
        
        return "=== INSUMO REGISTRADO ===\nID asignado: " + id + "\nImágenes guardadas: " + imgsRegistradas;
    }

    private String actualizar(String params) {
        String[] p = split(params, 3, "id,nombre,idProveedor");
        controller.actualizar(parseInt(p[0], "id"), p[1], parseInt(p[2], "idProveedor"));
        return "=== INSUMO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== INSUMO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
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
                + "Uso:\n  LISINSU[*]\n  REGINSU[nombre,idProveedor]\n"
                + "  ACTINSU[id,nombre,idProveedor]\n  ELIMINSU[id]";
    }
}
