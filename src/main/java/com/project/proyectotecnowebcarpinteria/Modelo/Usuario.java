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
public class Usuario {
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT * FROM usuario";

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

    //    Registrar usuario
    public int registrar(String nombre, String apellido, String email, String password, String telefono, String estado, int id_rol) {
        String query = "INSERT INTO usuario (nombre, apellido, email, password, telefono, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.setString(5, telefono);
            pst.setString(6, estado);
            pst.setInt(7, id_rol);

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

    // Actualizar usuario
    public void actualizar(int id, String nombre, String apellido, String email, String password, String telefono, String estado, int id_rol) {
        String query = "UPDATE usuario SET nombre = ?, apellido = ?, email = ?, password = ?, telefono = ?, rol = ?,  estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.setString(5, telefono);
            pst.setString(6, estado);
            pst.setInt(7, id_rol);
            pst.setInt(8, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Eliminar usuario
    public void eliminar(int id) {
        String query = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
