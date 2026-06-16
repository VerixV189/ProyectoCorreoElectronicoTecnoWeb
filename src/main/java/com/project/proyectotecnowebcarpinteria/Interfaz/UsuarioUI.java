package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.UsuarioController;
import java.util.List;

/**
 * UI de correo para el recurso Usuario.
 *
 * Comandos soportados:
 *   LISUSU[*]                                              → Lista todos los usuarios.
 *   REGUSU[nombre,apellido,email,password,telefono,estado,idRol]  → Registra un usuario.
 *   ACTUSU[id,nombre,apellido,email,password,telefono,estado,idRol] → Actualiza un usuario.
 *   ELIMUSU[id]                                            → Elimina un usuario.
 */
public class UsuarioUI implements ComandoUI {

    private final UsuarioController controller;

    public UsuarioUI() {
        this.controller = new UsuarioController();
    }

    @Override
    public String ejecutar(String comando, String parametros) {
        try {
            switch (comando) {
                case "LISUSU":  return listar();
                case "REGUSU":  return registrar(parametros);
                case "ACTUSU":  return actualizar(parametros);
                case "ELIMUSU": return eliminar(parametros);
                default:
                    return error(comando, "Comando no asociado a UsuarioUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------

    private String listar() {
        List<String[]> usuarios = controller.listar();
        if (usuarios == null || usuarios.isEmpty()) {
            return "=== LISTA DE USUARIOS ===\n(No hay usuarios registrados.)";
        }
        StringBuilder sb = new StringBuilder("=== LISTA DE USUARIOS ===\n");
        sb.append(String.format("%-5s %-15s %-15s %-30s %-12s %-10s%n",
                "ID", "Nombre", "Apellido", "Email", "Teléfono", "Estado"));
        sb.append("-".repeat(90)).append("\n");
        for (String[] u : usuarios) {
            sb.append(String.format("%-5s %-15s %-15s %-30s %-12s %-10s%n",
                    get(u, 0), get(u, 1), get(u, 2), get(u, 3), get(u, 4), get(u, 5)));
        }
        return sb.toString();
    }

    private String registrar(String params) {
        String[] p = split(params, 7, "REGUSU",
                "nombre,apellido,email,password,telefono,estado,idRol");
        int id = controller.registrar(p[0], p[1], p[2], p[3], p[4], p[5], parseInt(p[6], "idRol"));
        return "=== USUARIO REGISTRADO ===\nID asignado: " + id;
    }

    private String actualizar(String params) {
        String[] p = split(params, 8, "ACTUSU",
                "id,nombre,apellido,email,password,telefono,estado,idRol");
        controller.actualizar(parseInt(p[0], "id"), p[1], p[2], p[3], p[4], p[5], p[6],
                parseInt(p[7], "idRol"));
        return "=== USUARIO ACTUALIZADO ===\nID: " + p[0] + " actualizado correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== USUARIO ELIMINADO ===\nID: " + id + " eliminado correctamente.";
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------

    private String[] split(String params, int expected, String cmd, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) {
            throw new IllegalArgumentException(
                    "Se esperaban " + expected + " parámetros: " + formato
                    + "\nRecibidos: " + p.length + " → \"" + params + "\"");
        }
        return p;
    }

    private int parseInt(String value, String campo) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser un número entero. Recibido: \"" + value + "\"");
        }
    }

    private String get(String[] arr, int i) {
        return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-";
    }

    private String error(String comando, String motivo) {
        return "=== ERROR ===\n"
                + "Comando: " + comando + "\n"
                + "Motivo: " + motivo + "\n"
                + "=============\n"
                + "Uso correcto:\n"
                + "  LISUSU[*]\n"
                + "  REGUSU[nombre,apellido,email,password,telefono,estado,idRol]\n"
                + "  ACTUSU[id,nombre,apellido,email,password,telefono,estado,idRol]\n"
                + "  ELIMUSU[id]";
    }
}
