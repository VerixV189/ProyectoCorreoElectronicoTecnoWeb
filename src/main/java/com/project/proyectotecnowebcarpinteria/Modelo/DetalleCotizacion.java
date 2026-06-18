package com.project.proyectotecnowebcarpinteria.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para la tabla detalle_cotizacion.
 * Representa la asociación entre una cotización, un carpintero,
 * el precio ofertado y una descripción del trabajo.
 */
public class DetalleCotizacion {

    /**
     * Lista todos los detalles de una cotización específica.
     *
     * @param idCotizacion ID de la cotización a consultar.
     * @return Lista de filas [id, precio, descripcion, idCotizacion, idCarpintero, created_at, updated_at].
     */
    public List<String[]> listar(int idCotizacion) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT dc.id, dc.precio, dc.descripcion, dc.id_cotizacion, dc.id_carpintero, "
                + "u.nombre || ' ' || u.apellido AS carpintero, dc.created_at, dc.updated_at "
                + "FROM detalle_cotizacion dc "
                + "JOIN carpintero c ON dc.id_carpintero = c.id "
                + "JOIN usuario u ON c.id = u.id "
                + "WHERE dc.id_cotizacion = ? "
                + "ORDER BY dc.id";

        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, idCotizacion);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] fila = new String[8];
                fila[0] = String.valueOf(rs.getInt(1));   // id
                fila[1] = rs.getString(2);                // precio
                fila[2] = rs.getString(3);                // descripcion
                fila[3] = rs.getString(4);                // id_cotizacion
                fila[4] = rs.getString(5);                // id_carpintero
                fila[5] = rs.getString(6);                // nombre carpintero (JOIN)
                fila[6] = rs.getString(7);                // created_at
                fila[7] = rs.getString(8);                // updated_at
                lista.add(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    /**
     * Registra un nuevo detalle de cotización.
     *
     * @param precio         Precio ofertado por el carpintero.
     * @param descripcion    Descripción del trabajo.
     * @param idCotizacion   ID de la cotización padre.
     * @param idCarpintero   ID del carpintero asignado.
     * @return ID generado del detalle, o -1 si hubo error.
     */
    public int registrar(float precio, String descripcion, int idCotizacion, int idCarpintero) {
        String query = "INSERT INTO detalle_cotizacion (precio, descripcion, id_cotizacion, id_carpintero) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setFloat(1, precio);
            pst.setString(2, descripcion);
            pst.setInt(3, idCotizacion);
            pst.setInt(4, idCarpintero);
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
     * Actualiza un detalle de cotización existente.
     *
     * @param id             ID del detalle a actualizar.
     * @param precio         Nuevo precio.
     * @param descripcion    Nueva descripción.
     * @param idCotizacion   ID de la cotización padre.
     * @param idCarpintero   ID del carpintero asignado.
     */
    public void actualizar(int id, float precio, String descripcion, int idCotizacion, int idCarpintero) {
        String query = "UPDATE detalle_cotizacion SET precio = ?, descripcion = ?, "
                + "id_cotizacion = ?, id_carpintero = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setFloat(1, precio);
            pst.setString(2, descripcion);
            pst.setInt(3, idCotizacion);
            pst.setInt(4, idCarpintero);
            pst.setInt(5, id);

            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Elimina un detalle de cotización por su ID.
     *
     * @param id ID del detalle a eliminar.
     */
    public void eliminar(int id) {
        String query = "DELETE FROM detalle_cotizacion WHERE id = ?";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
