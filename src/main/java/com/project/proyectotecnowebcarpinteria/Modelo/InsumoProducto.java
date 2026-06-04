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
public class InsumoProducto {
    // Listar insumoProductos
    public List<String[]> listar(int id) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM insumo_producto ip, producto p WHERE ip.id_producto = p.id AND p.id = ?";
        
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] fila = new String[5];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Registrar insumo
    public int registrar(int id_insumo, int id_producto) {
        String query = "INSERT INTO insumo_producto (id_insumo, id_producto) VALUES (?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setInt(1, id_insumo);
            pst.setInt(2, id_producto);
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

    public void eliminar(int id) {
        String query = "DELETE FROM insumo_producto WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
}
