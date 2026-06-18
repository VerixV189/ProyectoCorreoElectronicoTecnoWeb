/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Pago;
import com.project.proyectotecnowebcarpinteria.Modelo.Venta;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de ventas.
 *
 * Además del CRUD estándar (registrar, actualizar, eliminar, listar), ofrece
 * dos métodos especializados con generación automática de pagos:
 *
 *   registrarContado(...) → para ventas al contado.
 *     Genera automáticamente 1 pago por el monto total con estado 'Pagado'.
 *
 *   registrarCredito(...) → para ventas a crédito.
 *     Genera automáticamente N pagos (mínimo 2) con estado 'Pendiente',
 *     distribuyendo el monto en cuotas iguales con el interés indicado
 *     y fechas de vencimiento cada 30 días.
 */
public class VentaController {
    Venta venta;
    Pago pago;

    public VentaController() {
        venta = new Venta();
        pago = new Pago();
    }

    public List<String[]> listar() {
        return venta.listar();
    }

    /**
     * Registra una venta de forma básica (sin generar pagos automáticos).
     * Para generar pagos automáticos, usar registrarContado() o registrarCredito().
     */
    public int registrar(String codigo, float totalCosto, Date fechaEntregado, String tipo, int idPedido, int idCliente) {
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(totalCosto, "totalCosto");
        ValidacionEntrada.fecha(fechaEntregado, "fechaEntregado");
        ValidacionEntrada.texto(tipo, "tipo");
        ValidacionEntrada.id(idPedido, "idPedido");
        ValidacionEntrada.id(idCliente, "idCliente");
        return venta.registrar(codigo, totalCosto, fechaEntregado, tipo, idPedido, idCliente);
    }

    /**
     * Registra una venta al CONTADO y genera automáticamente 1 pago.
     * El pago se crea con:
     *   - subtotal = totalCosto
     *   - interes = 0
     *   - estado = 'Pagado'
     *   - fecha_vencimiento = fechaEntregado
     *
     * @return Array [idVenta, idPago]
     */
    public int[] registrarContado(String codigo, float totalCosto, Date fechaEntregado, int idPedido, int idCliente) {
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(totalCosto, "totalCosto");
        ValidacionEntrada.fecha(fechaEntregado, "fechaEntregado");
        ValidacionEntrada.id(idPedido, "idPedido");
        ValidacionEntrada.id(idCliente, "idCliente");

        // 1. Registrar la venta
        int idVenta = venta.registrar(codigo, totalCosto, fechaEntregado, "Contado", idPedido, idCliente);
        if (idVenta <= 0) {
            throw new RuntimeException("Error al registrar la venta. Verifique los datos.");
        }

        // 2. Generar 1 pago automático
        int idPago = pago.registrar(totalCosto, 0f, "Pagado", fechaEntregado, idVenta);

        return new int[]{idVenta, idPago};
    }

    /**
     * Registra una venta a CRÉDITO y genera automáticamente N pagos (mínimo 2).
     * Cada cuota se calcula como:
     *   - subtotal = totalCosto / numeroPagos
     *   - interes = interesPorc (se guarda como valor numérico, ej: 5.0 para 5%)
     *   - estado = 'Pendiente'
     *   - fecha_vencimiento = fechaEntregado + (30 * i) días (i = 1, 2, ..., N)
     *
     * @param codigo         Código único de la venta.
     * @param totalCosto     Monto total de la venta.
     * @param fechaEntregado Fecha de entrega del pedido.
     * @param idPedido       ID del pedido asociado.
     * @param idCliente      ID del cliente comprador.
     * @param interesPorc    Porcentaje de interés por cuota (ej: 5.0 = 5%).
     * @param numeroPagos    Número de cuotas a generar (mínimo 2).
     * @return Array con [idVenta, idPago1, idPago2, ..., idPagoN].
     */
    public int[] registrarCredito(String codigo, float totalCosto, Date fechaEntregado,
                                   int idPedido, int idCliente, float interesPorc, int numeroPagos) {
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(totalCosto, "totalCosto");
        ValidacionEntrada.fecha(fechaEntregado, "fechaEntregado");
        ValidacionEntrada.id(idPedido, "idPedido");
        ValidacionEntrada.id(idCliente, "idCliente");
        ValidacionEntrada.noNegativo(interesPorc, "interes");

        if (numeroPagos < 2) {
            throw new IllegalArgumentException(
                    "Para ventas a credito se requieren al menos 2 pagos. Recibido: " + numeroPagos);
        }

        // 1. Registrar la venta
        int idVenta = venta.registrar(codigo, totalCosto, fechaEntregado, "Credito", idPedido, idCliente);
        if (idVenta <= 0) {
            throw new RuntimeException("Error al registrar la venta. Verifique los datos.");
        }

        // 2. Calcular cuota
        float cuota = totalCosto / numeroPagos;
        LocalDate baseDate = fechaEntregado.toLocalDate();

        // 3. Generar los N pagos con fechas de vencimiento cada 30 días
        List<Integer> ids = new ArrayList<>();
        ids.add(idVenta);

        for (int i = 1; i <= numeroPagos; i++) {
            LocalDate fechaVenc = baseDate.plusDays(30L * i);
            Date sqlFechaVenc = Date.valueOf(fechaVenc);
            int idPago = pago.registrar(cuota, interesPorc, "Pendiente", sqlFechaVenc, idVenta);
            ids.add(idPago);
        }

        return ids.stream().mapToInt(Integer::intValue).toArray();
    }

    public void actualizar(int id, String codigo, float totalCosto, Date fechaEntregado, String tipo, int idPedido, int idCliente) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(totalCosto, "totalCosto");
        ValidacionEntrada.fecha(fechaEntregado, "fechaEntregado");
        ValidacionEntrada.texto(tipo, "tipo");
        ValidacionEntrada.id(idPedido, "idPedido");
        ValidacionEntrada.id(idCliente, "idCliente");
        venta.actualizar(id, codigo, totalCosto, fechaEntregado, tipo, idPedido, idCliente);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        venta.eliminar(id);
    }
}
