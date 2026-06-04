/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Venta;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author erik
 */
public class VentaController {
    Venta venta;

    public VentaController() {
        venta = new Venta();
    }

    public List<String[]> listar() {
        return venta.listar();
    }

    public int registrar(String codigo, float totalCosto, Date fechaEntregado, String tipo, int idPedido, int idCliente) {
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(totalCosto, "totalCosto");
        ValidacionEntrada.fecha(fechaEntregado, "fechaEntregado");
        ValidacionEntrada.texto(tipo, "tipo");
        ValidacionEntrada.id(idPedido, "idPedido");
        ValidacionEntrada.id(idCliente, "idCliente");
        return venta.registrar(codigo, totalCosto, fechaEntregado, tipo, idPedido, idCliente);
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
