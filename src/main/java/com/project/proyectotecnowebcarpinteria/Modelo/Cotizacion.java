/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author erik
 */
public class Cotizacion {
    // Listar cotizaciones
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM cotizacion";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                fila[5] = rs.getString(6);
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Registrar cotizacion
    public int registrar(String descripcion, String estado, int id_cliente) {
        String query = "INSERT INTO cotizacion (descripcion, estado, id_cliente) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setString(1, descripcion);
            pst.setString(2, estado);
            pst.setInt(3, id_cliente);
            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    // Actualizar cotizacion
    public void actualizar(int id, String descripcion, String estado, int id_cliente) {
        String query = "UPDATE cotizacion SET descripcion = ?, estado= ?, id_cliente = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, descripcion);
            pst.setString(2, estado);
            pst.setInt(3, id_cliente);
            pst.setInt(4, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String query = "DELETE FROM cotizacion WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene el estado actual de una cotización.
     *
     * @param id ID de la cotización.
     * @return El estado como String (ej: "Pendiente", "Confirmada", "Cancelada"),
     *         o null si no existe.
     */
    public String obtenerEstado(int id) {
        String query = "SELECT estado FROM cotizacion WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza solo el campo estado de una cotización.
     * Usado automáticamente al registrar un pedido para marcarla como 'Confirmada'.
     *
     * @param id           ID de la cotización.
     * @param nuevoEstado  Nuevo estado a asignar (ej: "Confirmada").
     */
    public void actualizarEstado(int id, String nuevoEstado) {
        String query = "UPDATE cotizacion SET estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, nuevoEstado);
            pst.setInt(2, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
