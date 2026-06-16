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
public class Cliente {
    // Listar clientes
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT u.id, u.nombre, u.apellido, u.email, u.password, u.telefono, u.id_rol, u.estado, c.nit_facturacion, c.razon_social, c.direccion_envio, c.created_at, c.updated_at FROM cliente c, usuario u WHERE u.id = c.id";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[13];
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
                fila[10] = rs.getString(11);
                fila[11] = rs.getString(12);
                fila[12] = rs.getString(13);
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    //    Registrar cliente
    public int registrar(int id, String nit_facturacion, String razon_social, String direccion_envio) {
        String query = "INSERT INTO cliente (id, nit_facturacion, razon_social, direccion_envio) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setInt(1, id);
            pst.setString(2, nit_facturacion);
            pst.setString(3, razon_social);
            pst.setString(4, direccion_envio);

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

    // Actualizar cliente
    public void actualizar(int id, String nit_facturacion, String razon_social, String direccion_envio) {
        String query = "UPDATE cliente SET nit_facturacion = ?, razon_social = ?, direccion_envio = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, nit_facturacion);
            pst.setString(2, razon_social);
            pst.setString(3, direccion_envio);
            pst.setInt(4, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Eliminar cliente
    public void eliminar(int id) {
        String query = "DELETE FROM cliente WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
}
