package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.CarpinteroController;
import java.util.List;

/**
 * UI de correo para el recurso Carpintero.
 *
 * Comandos soportados:
 *   LISCARP[*]
 *   REGCARP[nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol]
 *   ACTCARP[id,nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol]
 *   ELIMCARP[id]
 */
public class CarpinteroUI implements ComandoUI {

    private final CarpinteroController controller;

    public CarpinteroUI() {
        this.controller = new CarpinteroController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISCARP":  return listar();
                case "REGCARP":  return registrar(parametros);
                case "ACTCARP":  return actualizar(parametros);
                case "ELIMCARP": return eliminar(parametros);
                default: return error(comando, "Comando no asociado a CarpinteroUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) {
            return "=== LISTA DE CARPINTEROS ===\n(No hay carpinteros registrados.)";
        }
        StringBuilder sb = new StringBuilder("=== LISTA DE CARPINTEROS ===\n");
        sb.append(String.format("%-5s %-15s %-15s %-25s %-20s %-10s%n",
                "ID", "Nombre", "Apellido", "Email", "Especialidad", "Costo/h"));
        sb.append("-".repeat(95)).append("\n");
        for (String[] c : lista) {
            sb.append(String.format("%-5s %-15s %-15s %-25s %-20s %-10s%n",
                    get(c, 0), get(c, 1), get(c, 2), get(c, 3), get(c, 4), get(c, 5)));
        }
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 9, "nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol");
        int id = controller.registrar(p[0], p[1], p[2], p[3], p[4], p[5], p[6],
                parseFloat(p[7], "costoHora"), parseInt(p[8], "idRol"));
        return "=== CARPINTERO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 10, "id,nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                parseFloat(p[8], "costoHora"), parseInt(p[9], "idRol"));
        return "=== CARPINTERO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== CARPINTERO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) {
            throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        }
        return p;
    }

    private int parseInt(String value, String campo) {
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + campo + "' debe ser entero. Recibido: \"" + value + "\""); }
    }

    private float parseFloat(String value, String campo) {
        try { return Float.parseFloat(value.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + campo + "' debe ser numérico. Recibido: \"" + value + "\""); }
    }

    private String get(String[] arr, int i) {
        return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-";
    }

    private String error(String comando, String motivo) {
        return "=== ERROR ===\nComando: " + comando + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso correcto:\n"
                + "  LISCARP[*]\n"
                + "  REGCARP[nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol]\n"
                + "  ACTCARP[id,nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol]\n"
                + "  ELIMCARP[id]";
    }
}
