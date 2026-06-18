/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Pago;
import java.sql.Date;
import java.util.List;

/**
 * Controlador para la gestión de pagos.
 * Incluye el campo fecha_vencimiento para soportar el cálculo de mora en CU7.
 */
public class PagoController {
    Pago pago;

    public PagoController() {
        pago = new Pago();
    }

    public List<String[]> listar() {
        return pago.listar();
    }

    /**
     * Registra un pago manual.
     *
     * @param subtotal         Monto de la cuota sin interés.
     * @param interes          Interés aplicado.
     * @param estado           Estado del pago ('Pendiente', 'Pagado', etc.).
     * @param fechaVencimiento Fecha límite de pago (puede ser null para pagos inmediatos).
     * @param idVenta          ID de la venta asociada.
     * @return ID generado del pago.
     */
    public int registrar(float subtotal, float interes, String estado, Date fechaVencimiento, int idVenta) {
        ValidacionEntrada.noNegativo(subtotal, "subtotal");
        ValidacionEntrada.noNegativo(interes, "interes");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.id(idVenta, "idVenta");
        return pago.registrar(subtotal, interes, estado, fechaVencimiento, idVenta);
    }

    public void actualizar(int id, float subtotal, float interes, String estado, Date fechaVencimiento, int idVenta) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.noNegativo(subtotal, "subtotal");
        ValidacionEntrada.noNegativo(interes, "interes");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.id(idVenta, "idVenta");
        pago.actualizar(id, subtotal, interes, estado, fechaVencimiento, idVenta);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        pago.eliminar(id);
    }
}
