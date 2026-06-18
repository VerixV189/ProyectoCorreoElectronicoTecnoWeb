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
 * Modelo para la tabla pago.
 * Incluye el campo fecha_vencimiento para el cálculo de mora en CU7.
 */
public class Pago {
    /**
     * Lista todos los pagos registrados, incluyendo fecha_vencimiento.
     *
     * @return Lista de filas [id, subtotal, interes, estado, fecha_vencimiento, id_venta, created_at].
     */
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT id, subtotal, interes, estado, fecha_vencimiento, id_venta, created_at FROM pago ORDER BY id";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt(1));   // id
                fila[1] = rs.getString(2);                // subtotal
                fila[2] = rs.getString(3);                // interes
                fila[3] = rs.getString(4);                // estado
                fila[4] = rs.getString(5);                // fecha_vencimiento
                fila[5] = rs.getString(6);                // id_venta
                fila[6] = rs.getString(7);                // created_at
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    /**
     * Registra un nuevo pago con fecha de vencimiento.
     *
     * @param subtotal         Monto de la cuota sin interés.
     * @param interes          Interés aplicado a la cuota.
     * @param estado           Estado del pago (ej: 'Pendiente', 'Pagado').
     * @param fechaVencimiento Fecha límite de pago. Puede ser null.
     * @param idVenta          ID de la venta a la que pertenece este pago.
     * @return ID generado del pago, o -1 si hubo error.
     */
    public int registrar(float subtotal, float interes, String estado, Date fechaVencimiento, int idVenta) {
        String query = "INSERT INTO pago (subtotal, interes, estado, fecha_vencimiento, id_venta) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setFloat(1, subtotal);
            pst.setFloat(2, interes);
            pst.setString(3, estado);
            if (fechaVencimiento != null) {
                pst.setDate(4, fechaVencimiento);
            } else {
                pst.setNull(4, java.sql.Types.DATE);
            }
            pst.setInt(5, idVenta);
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

    /**
     * Actualiza un pago existente.
     *
     * @param id               ID del pago a actualizar.
     * @param subtotal         Nuevo subtotal.
     * @param interes          Nuevo interés.
     * @param estado           Nuevo estado.
     * @param fechaVencimiento Nueva fecha de vencimiento. Puede ser null.
     * @param idVenta          ID de la venta asociada.
     */
    public void actualizar(int id, float subtotal, float interes, String estado, Date fechaVencimiento, int idVenta) {
        String query = "UPDATE pago SET subtotal = ?, interes = ?, estado = ?, fecha_vencimiento = ?, id_venta = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setFloat(1, subtotal);
            pst.setFloat(2, interes);
            pst.setString(3, estado);
            if (fechaVencimiento != null) {
                pst.setDate(4, fechaVencimiento);
            } else {
                pst.setNull(4, java.sql.Types.DATE);
            }
            pst.setInt(5, idVenta);
            pst.setInt(6, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String query = "DELETE FROM pago WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
