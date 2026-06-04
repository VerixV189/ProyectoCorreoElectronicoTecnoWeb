/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author erik
 */
public class Pedido {
    // Listar pedidos
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM pedido";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                fila[5] = rs.getString(6);
                fila[6] = rs.getString(7);
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Registrar pedido
    public int registrar(String codigo, float precio, Date fecha_entrega, int id_cotizacion) {
        String query = "INSERT INTO pedido (codigo, precio, fecha_entrega, id_cotizacion) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setString(1, codigo);
            pst.setFloat(2, precio);
            pst.setDate(3, fecha_entrega);
            pst.setInt(4, id_cotizacion);
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

    // Actualizar pedido
    public void actualizar(int id, String codigo, float precio, Date fecha_entrega, int id_cotizacion) {
        String query = "UPDATE pedido SET codigo = ?, precio= ?, fecha_entrega= ?, id_cotizacion = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, codigo);
            pst.setFloat(2, precio);
            pst.setDate(3, fecha_entrega);
            pst.setInt(4, id_cotizacion);
            pst.setInt(5, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String query = "DELETE FROM pedido WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
}
