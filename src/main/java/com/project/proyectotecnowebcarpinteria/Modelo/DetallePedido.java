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
public class DetallePedido {
    // Listar detalles de pedido
    public List<String[]> listar(int id) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM detalle_pedido dp, pedido p where dp.id_pedido = p.id AND p.id = ?";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[9];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                fila[5] = rs.getString(6);
                fila[6] = rs.getString(7);
                fila[7] = rs.getString(8);
                fila[8] = rs.getString(9);
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Registrar detalle de pedido
    public int registrar(int cantidad, float precio, String estado, String descripcion, int id_pedido, int id_producto) {
        String query = "INSERT INTO detalle_pedido (cantidad, precio, estado, descripcion, id_pedido, id_producto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setInt(1, cantidad);
            pst.setFloat(2, precio);
            pst.setString(3, estado);
            pst.setString(4, descripcion);
            pst.setInt(5, id_pedido);
            if (id_producto <= 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, id_producto);
            }
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

    // Actualizar detalle de pedido
    public void actualizar(int id, int cantidad, float precio, String estado, String descripcion, int id_pedido, int id_producto) {
        String query = "UPDATE detalle_pedido SET cantidad = ?, precio = ?, estado = ?, descripcion = ?, id_pedido = ?, id_producto = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, cantidad);
            pst.setFloat(2, precio);
            pst.setString(3, estado);
            pst.setString(4, descripcion);
            pst.setInt(5, id_pedido);
            if (id_producto <= 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, id_producto);
            }
            pst.setInt(7, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String query = "DELETE FROM detalle_pedido WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
}
