package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Modelo.Reporte;
import java.util.List;

/**
 * UI de correo para los reportes del sistema de carpintería.
 * (Fechas en formato YYYY-MM-DD)
 *
 * Comandos soportados:
 *   REPVENTOT[fechaInicio,fechaFin]                  - Ventas totales por rango de fechas
 *   REPVENPROD[fechaInicio,fechaFin]                  - Ventas por producto
 *   REPCOTCARP[fechaInicio,fechaFin]                  - Cotizaciones por carpintero
 *   REPDETPED[idPedido]                               - Detalle de venta por pedido
 *   REPPAGVEN[fechaInicio,fechaFin]                   - Pagos por venta
 *   REPPRODINV[idTipo]                                - Productos en inventario (0 = todos)
 *   REPINSUPROV[idProveedor]                          - Insumos por proveedor (0 = todos)
 *   REPPEDFEC[fechaInicio,fechaFin]                   - Pedidos por rango de fechas
 */
public class ReporteUI implements ComandoUI {

    private final Reporte reporte;

    public ReporteUI() {
        this.reporte = new Reporte();
    }

    @Override
    public String ejecutar(String comando, String parametros, java.util.List<String> imagenesAdjuntas) {
        try {
            switch (comando) {
                case "REPVENTOT":   return reporteVentasTotales(parametros);
                case "REPVENPROD":  return reporteVentasProducto(parametros);
                case "REPCOTCARP":  return reporteCotizacionesCarpintero(parametros);
                case "REPDETPED":   return reporteDetallePedido(parametros);
                case "REPPAGVEN":   return reportePagosVenta(parametros);
                case "REPPRODINV":  return reporteProductosInventario(parametros);
                case "REPINSUPROV": return reporteInsumosProveedor(parametros);
                case "REPPEDFEC":   return reportePedidosFechas(parametros);
                default: return error(comando, "Comando de reporte no reconocido.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    // =========================================================================
    // 1. Ventas Totales
    // =========================================================================
    private String reporteVentasTotales(String params) {
        String[] p = split(params, 2, "fechaInicio,fechaFin");
        List<String[]> lista = reporte.ventasTotales(p[0].trim(), p[1].trim());
        if (lista.isEmpty()) return "=== REPORTE: VENTAS TOTALES ===\nPeriodo: " + p[0].trim() + " a " + p[1].trim() + "\n(No se encontraron ventas en este periodo.)";

        StringBuilder sb = new StringBuilder("=== REPORTE: VENTAS TOTALES ===\n");
        sb.append("Periodo: ").append(p[0].trim()).append(" a ").append(p[1].trim()).append("\n\n");
        sb.append(String.format("%-5s %-12s %-12s %-12s %-10s %-25s%n", "ID", "Código", "Total", "Fecha", "Tipo", "Cliente"));
        sb.append("-".repeat(80)).append("\n");

        double granTotal = 0;
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-12s %-12s %-12s %-10s %-25s%n", get(f,0), get(f,1), get(f,2), get(f,3), get(f,4), get(f,5)));
            try { granTotal += Double.parseDouble(f[2]); } catch (Exception ignored) {}
        }
        sb.append("-".repeat(80)).append("\n");
        sb.append(String.format("Total de ventas: %d | Monto total: %.2f%n", lista.size(), granTotal));
        return sb.toString();
    }

    // =========================================================================
    // 2. Ventas por Producto
    // =========================================================================
    private String reporteVentasProducto(String params) {
        String[] p = split(params, 2, "fechaInicio,fechaFin");
        List<String[]> lista = reporte.ventasProducto(p[0].trim(), p[1].trim());
        if (lista.isEmpty()) return "=== REPORTE: VENTAS POR PRODUCTO ===\nPeriodo: " + p[0].trim() + " a " + p[1].trim() + "\n(No se encontraron ventas de productos en este periodo.)";

        StringBuilder sb = new StringBuilder("=== REPORTE: VENTAS POR PRODUCTO ===\n");
        sb.append("Periodo: ").append(p[0].trim()).append(" a ").append(p[1].trim()).append("\n\n");
        sb.append(String.format("%-5s %-25s %-15s %-15s%n", "ID", "Producto", "Uds. Vendidas", "Ingreso Total"));
        sb.append("-".repeat(65)).append("\n");
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-25s %-15s %-15s%n", get(f,0), get(f,1), get(f,2), get(f,3)));
        }
        return sb.toString();
    }

    // =========================================================================
    // 3. Cotizaciones por Carpintero
    // =========================================================================
    private String reporteCotizacionesCarpintero(String params) {
        String[] p = split(params, 2, "fechaInicio,fechaFin");
        List<String[]> lista = reporte.cotizacionesCarpintero(p[0].trim(), p[1].trim());
        if (lista.isEmpty()) return "=== REPORTE: COTIZACIONES POR CARPINTERO ===\nPeriodo: " + p[0].trim() + " a " + p[1].trim() + "\n(No se encontraron cotizaciones en este periodo.)";

        StringBuilder sb = new StringBuilder("=== REPORTE: COTIZACIONES POR CARPINTERO ===\n");
        sb.append("Periodo: ").append(p[0].trim()).append(" a ").append(p[1].trim()).append("\n\n");
        sb.append(String.format("%-5s %-25s %-20s %-15s%n", "ID", "Carpintero", "Total Cotizaciones", "Monto Total"));
        sb.append("-".repeat(70)).append("\n");
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-25s %-20s %-15s%n", get(f,0), get(f,1), get(f,2), get(f,3)));
        }
        return sb.toString();
    }

    // =========================================================================
    // 4. Detalle de Venta por Pedido
    // =========================================================================
    private String reporteDetallePedido(String params) {
        int idPedido = parseInt(params.trim(), "idPedido");
        List<String[]> lista = reporte.detalleVentaPedido(idPedido);
        if (lista.isEmpty()) return "=== REPORTE: DETALLE PEDIDO #" + idPedido + " ===\n(No se encontraron detalles para este pedido.)";

        StringBuilder sb = new StringBuilder("=== REPORTE: DETALLE PEDIDO #" + idPedido + " ===\n\n");
        sb.append(String.format("%-5s %-20s %-8s %-10s %-12s %-12s %-20s%n", "ID", "Producto", "Cant.", "Precio", "Subtotal", "Estado", "Descripción"));
        sb.append("-".repeat(90)).append("\n");

        double total = 0;
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-20s %-8s %-10s %-12s %-12s %-20s%n", get(f,0), get(f,1), get(f,2), get(f,3), get(f,4), get(f,5), get(f,6)));
            try { total += Double.parseDouble(f[4]); } catch (Exception ignored) {}
        }
        sb.append("-".repeat(90)).append("\n");
        sb.append(String.format("Items: %d | Total: %.2f%n", lista.size(), total));
        return sb.toString();
    }

    // =========================================================================
    // 5. Pagos por Venta
    // =========================================================================
    private String reportePagosVenta(String params) {
        String[] p = split(params, 2, "fechaInicio,fechaFin");
        List<String[]> lista = reporte.pagosVenta(p[0].trim(), p[1].trim());
        if (lista.isEmpty()) return "=== REPORTE: PAGOS POR VENTA ===\nPeriodo: " + p[0].trim() + " a " + p[1].trim() + "\n(No se encontraron pagos en este periodo.)";

        StringBuilder sb = new StringBuilder("=== REPORTE: PAGOS POR VENTA ===\n");
        sb.append("Periodo: ").append(p[0].trim()).append(" a ").append(p[1].trim()).append("\n\n");
        sb.append(String.format("%-5s %-12s %-10s %-10s %-12s %-12s %-10s%n", "ID", "Venta", "Subtotal", "Interés", "Total Pago", "Estado", "Tipo"));
        sb.append("-".repeat(75)).append("\n");
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-12s %-10s %-10s %-12s %-12s %-10s%n", get(f,0), get(f,1), get(f,2), get(f,3), get(f,4), get(f,5), get(f,6)));
        }
        return sb.toString();
    }

    // =========================================================================
    // 6. Productos en Inventario
    // =========================================================================
    private String reporteProductosInventario(String params) {
        int idTipo = parseInt(params.trim(), "idTipo");
        List<String[]> lista = reporte.productosInventario(idTipo);
        if (lista.isEmpty()) return "=== REPORTE: INVENTARIO DE PRODUCTOS ===\n(No se encontraron productos" + (idTipo > 0 ? " para el tipo " + idTipo : "") + ".)";

        StringBuilder sb = new StringBuilder("=== REPORTE: INVENTARIO DE PRODUCTOS ===\n");
        if (idTipo > 0) sb.append("Filtro: Tipo ID ").append(idTipo).append("\n");
        else sb.append("Mostrando todos los tipos\n");
        sb.append("\n");
        sb.append(String.format("%-5s %-20s %-15s %-8s %-10s %-15s %-10s%n", "ID", "Producto", "Tipo", "Stock", "Precio", "Valor Inv.", "Estado"));
        sb.append("-".repeat(90)).append("\n");
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-20s %-15s %-8s %-10s %-15s %-10s%n", get(f,0), get(f,1), get(f,2), get(f,3), get(f,4), get(f,5), get(f,6)));
        }
        return sb.toString();
    }

    // =========================================================================
    // 7. Insumos por Proveedor
    // =========================================================================
    private String reporteInsumosProveedor(String params) {
        int idProveedor = parseInt(params.trim(), "idProveedor");
        List<String[]> lista = reporte.insumosProveedor(idProveedor);
        if (lista.isEmpty()) return "=== REPORTE: INSUMOS POR PROVEEDOR ===\n(No se encontraron insumos" + (idProveedor > 0 ? " para el proveedor " + idProveedor : "") + ".)";

        StringBuilder sb = new StringBuilder("=== REPORTE: INSUMOS POR PROVEEDOR ===\n");
        if (idProveedor > 0) sb.append("Filtro: Proveedor ID ").append(idProveedor).append("\n");
        else sb.append("Mostrando todos los proveedores\n");
        sb.append("\n");
        sb.append(String.format("%-5s %-20s %-25s %-15s %-25s%n", "ID", "Insumo", "Proveedor", "Teléfono", "Dirección"));
        sb.append("-".repeat(95)).append("\n");
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-20s %-25s %-15s %-25s%n", get(f,0), get(f,1), get(f,2), get(f,3), get(f,4)));
        }
        return sb.toString();
    }

    // =========================================================================
    // 8. Pedidos por Fechas
    // =========================================================================
    private String reportePedidosFechas(String params) {
        String[] p = split(params, 2, "fechaInicio,fechaFin");
        List<String[]> lista = reporte.pedidosFechas(p[0].trim(), p[1].trim());
        if (lista.isEmpty()) return "=== REPORTE: PEDIDOS POR FECHA ===\nPeriodo: " + p[0].trim() + " a " + p[1].trim() + "\n(No se encontraron pedidos en este periodo.)";

        StringBuilder sb = new StringBuilder("=== REPORTE: PEDIDOS POR FECHA ===\n");
        sb.append("Periodo: ").append(p[0].trim()).append(" a ").append(p[1].trim()).append("\n\n");
        sb.append(String.format("%-5s %-12s %-10s %-12s %-25s %-25s%n", "ID", "Código", "Precio", "F. Entrega", "Cotización", "Cliente"));
        sb.append("-".repeat(95)).append("\n");
        for (String[] f : lista) {
            sb.append(String.format("%-5s %-12s %-10s %-12s %-25s %-25s%n", get(f,0), get(f,1), get(f,2), get(f,3), get(f,4), get(f,5)));
        }
        return sb.toString();
    }

    // =========================================================================
    // Utilidades
    // =========================================================================
    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parámetros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }
    private String error(String cmd, String motivo) {
        return "=== ERROR EN REPORTE ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Reportes disponibles (Fechas: YYYY-MM-DD):\n"
                + "  REPVENTOT[fechaInicio,fechaFin]\n"
                + "  REPVENPROD[fechaInicio,fechaFin]\n"
                + "  REPCOTCARP[fechaInicio,fechaFin]\n"
                + "  REPDETPED[idPedido]\n"
                + "  REPPAGVEN[fechaInicio,fechaFin]\n"
                + "  REPPRODINV[idTipo]  (0 = todos)\n"
                + "  REPINSUPROV[idProveedor]  (0 = todos)\n"
                + "  REPPEDFEC[fechaInicio,fechaFin]";
    }
}
