package com.project.proyectotecnowebcarpinteria.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de ejecutar las consultas SQL para los reportes del sistema.
 * Cada método devuelve una lista de filas (String[]) listas para formatear.
 *
 * @author erik
 */
public class Reporte {

    // =========================================================================
    // 1. REPORTE DE VENTAS TOTALES (por rango de fechas)
    // =========================================================================
    /**
     * Ventas realizadas entre dos fechas, mostrando cliente y totales.
     */
    public List<String[]> ventasTotales(String fechaInicio, String fechaFin) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT v.id, v.codigo, v.total_costo, v.fecha_entregado, v.tipo, "
                + "u.nombre || ' ' || u.apellido AS cliente "
                + "FROM venta v "
                + "JOIN cliente c ON v.id_cliente = c.id "
                + "JOIN usuario u ON c.id = u.id "
                + "WHERE v.fecha_entregado BETWEEN ?::date AND ?::date "
                + "ORDER BY v.fecha_entregado";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
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
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 2. REPORTE DE VENTAS POR PRODUCTO (por rango de fechas)
    // =========================================================================
    /**
     * Cuántas unidades de cada producto se vendieron y el ingreso total.
     */
    public List<String[]> ventasProducto(String fechaInicio, String fechaFin) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, SUM(dp.cantidad) AS total_vendido, "
                + "SUM(dp.cantidad * dp.precio) AS ingreso_total "
                + "FROM detalle_pedido dp "
                + "JOIN producto p ON dp.id_producto = p.id "
                + "JOIN pedido ped ON dp.id_pedido = ped.id "
                + "JOIN venta v ON v.id_pedido = ped.id "
                + "WHERE v.fecha_entregado BETWEEN ?::date AND ?::date "
                + "GROUP BY p.id, p.nombre "
                + "ORDER BY ingreso_total DESC";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = String.valueOf(rs.getInt(3));
                fila[3] = rs.getString(4);
                lista.add(fila);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 3. REPORTE DE COTIZACIONES POR CARPINTERO (por rango de fechas)
    // =========================================================================
    /**
     * Cotizaciones detalladas atendidas por cada carpintero.
     */
    public List<String[]> cotizacionesCarpintero(String fechaInicio, String fechaFin) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT u.id, u.nombre || ' ' || u.apellido AS carpintero, "
                + "COUNT(dc.id) AS total_cotizaciones, "
                + "SUM(dc.precio) AS monto_total "
                + "FROM detalle_cotizacion dc "
                + "JOIN carpintero carp ON dc.id_carpintero = carp.id "
                + "JOIN usuario u ON carp.id = u.id "
                + "JOIN cotizacion cot ON dc.id_cotizacion = cot.id "
                + "WHERE cot.created_at BETWEEN ?::date AND (?::date + INTERVAL '1 day') "
                + "GROUP BY u.id, u.nombre, u.apellido "
                + "ORDER BY total_cotizaciones DESC";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[4];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = String.valueOf(rs.getInt(3));
                fila[3] = rs.getString(4);
                lista.add(fila);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 4. REPORTE DE DETALLE DE VENTA POR PEDIDO
    // =========================================================================
    /**
     * Todos los detalles de productos incluidos en un pedido/venta dado.
     */
    public List<String[]> detalleVentaPedido(int idPedido) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT dp.id, p.nombre AS producto, dp.cantidad, dp.precio, "
                + "(dp.cantidad * dp.precio) AS subtotal, dp.estado, dp.descripcion "
                + "FROM detalle_pedido dp "
                + "LEFT JOIN producto p ON dp.id_producto = p.id "
                + "WHERE dp.id_pedido = ? "
                + "ORDER BY dp.id";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = String.valueOf(rs.getInt(3));
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                fila[5] = rs.getString(6);
                fila[6] = rs.getString(7);
                lista.add(fila);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 5. REPORTE DE PAGOS POR VENTA (por rango de fechas)
    // =========================================================================
    /**
     * Pagos realizados junto con datos de la venta asociada.
     */
    public List<String[]> pagosVenta(String fechaInicio, String fechaFin) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT pa.id, v.codigo AS venta, pa.subtotal, pa.interes, "
                + "(pa.subtotal + pa.interes) AS total_pago, pa.estado, v.tipo "
                + "FROM pago pa "
                + "JOIN venta v ON pa.id_venta = v.id "
                + "WHERE v.fecha_entregado BETWEEN ?::date AND ?::date "
                + "ORDER BY pa.id";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
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
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 6. REPORTE DE PRODUCTOS POR TIPO (inventario actual)
    // =========================================================================
    /**
     * Productos agrupados por tipo con stock y precio.
     */
    public List<String[]> productosInventario(int idTipo) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, t.nombre AS tipo, p.cantidad, p.precio, "
                + "(p.cantidad * p.precio) AS valor_inventario, p.estado "
                + "FROM producto p "
                + "JOIN tipo t ON p.id_tipo = t.id "
                + (idTipo > 0 ? "WHERE p.id_tipo = ? " : "")
                + "ORDER BY t.nombre, p.nombre";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            if (idTipo > 0) ps.setInt(1, idTipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = String.valueOf(rs.getInt(4));
                fila[4] = rs.getString(5);
                fila[5] = rs.getString(6);
                fila[6] = rs.getString(7);
                lista.add(fila);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 7. REPORTE DE INSUMOS POR PROVEEDOR
    // =========================================================================
    /**
     * Insumos agrupados por proveedor.
     */
    public List<String[]> insumosProveedor(int idProveedor) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT i.id, i.nombre, prov.nombre_empresa AS proveedor, "
                + "prov.telefono, prov.direccion "
                + "FROM insumo i "
                + "JOIN proveedor prov ON i.id_proveedor = prov.id "
                + (idProveedor > 0 ? "WHERE i.id_proveedor = ? " : "")
                + "ORDER BY prov.nombre_empresa, i.nombre";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            if (idProveedor > 0) ps.setInt(1, idProveedor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[5];
                fila[0] = String.valueOf(rs.getInt(1));
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(5);
                lista.add(fila);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    // =========================================================================
    // 8. REPORTE DE PEDIDOS POR ESTADO (por rango de fechas)
    // =========================================================================
    /**
     * Pedidos y sus detalles en un rango de fechas.
     */
    public List<String[]> pedidosFechas(String fechaInicio, String fechaFin) {
        List<String[]> lista = new ArrayList<>();
        String query = "SELECT ped.id, ped.codigo, ped.precio, ped.fecha_entrega, "
                + "cot.descripcion AS cotizacion, "
                + "u.nombre || ' ' || u.apellido AS cliente "
                + "FROM pedido ped "
                + "JOIN cotizacion cot ON ped.id_cotizacion = cot.id "
                + "JOIN cliente cli ON cot.id_cliente = cli.id "
                + "JOIN usuario u ON cli.id = u.id "
                + "WHERE ped.fecha_entrega BETWEEN ?::date AND ?::date "
                + "ORDER BY ped.fecha_entrega";
        try (Connection con = Conexion.getConnection()) {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
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
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }
}
