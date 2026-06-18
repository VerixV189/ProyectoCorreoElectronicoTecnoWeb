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
public class Producto {
    // Listar productos
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.cantidad, p.precio, p.descripcion, p.estado, p.id_tipo, p.created_at, p.updated_at, COALESCE(STRING_AGG(img.url, ', '), 'Sin imagen') as imagenes " +
                       "FROM producto p LEFT JOIN imagen img ON p.id = img.id_producto " +
                       "GROUP BY p.id, p.nombre, p.cantidad, p.precio, p.descripcion, p.estado, p.id_tipo, p.created_at, p.updated_at " +
                       "ORDER BY p.id";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[10];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                fila[5] = rs.getString(6);
                fila[6] = rs.getString(7);
                fila[7] = rs.getString(8);
                fila[8] = rs.getString(9);
                fila[9] = rs.getString(10);
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Registrar producto
    public int registrar(String nombre, int cantidad, float precio, String descripcion, String estado, int id_tipo) {
        String query = "INSERT INTO producto (nombre, cantidad, precio, descripcion, estado, id_tipo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setString(1, nombre);
            pst.setInt(2, cantidad);
            pst.setFloat(3, precio);
            pst.setString(4, descripcion);
            pst.setString(5, estado);
            pst.setInt(6, id_tipo);
            
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

    // Actualizar producto
    public void actualizar(int id, String nombre, int cantidad, float precio, String descripcion, String estado, int id_tipo) {
        String query = "UPDATE producto SET nombre = ?, cantidad = ?, precio = ?, descripcion = ?, estado = ?, id_tipo = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, nombre);
            pst.setInt(2, cantidad);
            pst.setFloat(3, precio);
            pst.setString(4, descripcion);
            pst.setString(5, estado);
            pst.setInt(6, id_tipo);
            pst.setInt(7, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String query = "DELETE FROM producto WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
}
