/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Pago;
import java.util.List;

/**
 *
 * @author erik
 */
public class PagoController {
    Pago pago;

    public PagoController() {
        pago = new Pago();
    }

    public List<String[]> listar() {
        return pago.listar();
    }

    public int registrar(float subtotal, float interes, int idVenta) {
        ValidacionEntrada.noNegativo(subtotal, "subtotal");
        ValidacionEntrada.noNegativo(interes, "interes");
        ValidacionEntrada.id(idVenta, "idVenta");
        return pago.registrar(subtotal, interes, idVenta);
    }

    public void actualizar(int id, float subtotal, float interes, int idVenta) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.noNegativo(subtotal, "subtotal");
        ValidacionEntrada.noNegativo(interes, "interes");
        ValidacionEntrada.id(idVenta, "idVenta");
        pago.actualizar(id, subtotal, interes, idVenta);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        pago.eliminar(id);
    }
}
