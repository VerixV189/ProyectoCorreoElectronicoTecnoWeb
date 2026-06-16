package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.CotizacionController;
import java.util.List;

/**
 * UI de correo para el recurso Cotizacion.
 *
 * Comandos soportados:
 *   LISCOT[*]
 *   REGCOT[descripcion,estado,idCliente,idCarpintero]
 *   ACTCOT[id,descripcion,estado,idCliente,idCarpintero]
 *   ELIMCOT[id]
 */
public class CotizacionUI implements ComandoUI {

    private final CotizacionController controller;

    public CotizacionUI() {
        this.controller = new CotizacionController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISCOT":  return listar();
                case "REGCOT":  return registrar(parametros);
                case "ACTCOT":  return actualizar(parametros);
                case "ELIMCOT": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a CotizacionUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE COTIZACIONES ===\n(No hay cotizaciones registradas.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE COTIZACIONES ===\n");
        sb.append(String.format("%-5s %-30s %-12s %-10s %-10s%n", "ID", "Descripción", "Estado", "idCliente", "idCarpintero"));
        sb.append("-".repeat(70)).append("\n");
        for (String[] c : lista) sb.append(String.format("%-5s %-30s %-12s %-10s %-10s%n", get(c,0), get(c,1), get(c,2), get(c,3), get(c,4)));
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 4, "descripcion,estado,idCliente,idCarpintero");
        int id = controller.registrar(p[0], p[1], parseInt(p[2], "idCliente"), parseInt(p[3], "idCarpintero"));
        return "=== COTIZACIÓN REGISTRADA ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 5, "id,descripcion,estado,idCliente,idCarpintero");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2], parseInt(p[3], "idCliente"), parseInt(p[4], "idCarpintero"));
        return "=== COTIZACIÓN ACTUALIZADA ===\nID: " + p[0] + " actualizada correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== COTIZACIÓN ELIMINADA ===\nID: " + id + " eliminada correctamente.";
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
                + "Uso:\n  LISCOT[*]\n  REGCOT[descripcion,estado,idCliente,idCarpintero]\n"
                + "  ACTCOT[id,descripcion,estado,idCliente,idCarpintero]\n  ELIMCOT[id]";
    }
}
