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
public class Carpintero {
    // Listar carpinteros
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT u.id, u.nombre, u.apellido, u.email, u.password, u.telefono, u.id_rol, u.estado, c.especialidad, c.costo_hora, c.created_at, c.updated_at FROM carpintero c, usuario u WHERE u.id = c.id";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[12];
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
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    //    Registrar carpinteros
    public int registrar(int id, String especialidad, float costo_hora) {
        String query = "INSERT INTO carpintero (id, especialidad, costo_hora) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setInt(1, id);
            pst.setString(2, especialidad);
            pst.setFloat(3, costo_hora);

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

    // Actualizar carpintero
    public void actualizar(int id, String especialidad, float costo_hora) {
        String query = "UPDATE carpintero SET especialidad = ?, costo_hora = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, especialidad);
            pst.setFloat(2, costo_hora);
            pst.setInt(3, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Eliminar carpintero
    public void eliminar(int id) {
        String query = "DELETE FROM carpintero WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    
}
