/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Pedido;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author erik
 */
public class PedidoController {
    Pedido pedido;

    public PedidoController() {
        pedido = new Pedido();
    }

    public List<String[]> listar() {
        return pedido.listar();
    }

    public int registrar(String codigo, float precio, Date fechaEntrega, int idCotizacion) {
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.fecha(fechaEntrega, "fechaEntrega");
        ValidacionEntrada.id(idCotizacion, "idCotizacion");
        return pedido.registrar(codigo, precio, fechaEntrega, idCotizacion);
    }

    public void actualizar(int id, String codigo, float precio, Date fechaEntrega, int idCotizacion) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.fecha(fechaEntrega, "fechaEntrega");
        ValidacionEntrada.id(idCotizacion, "idCotizacion");
        pedido.actualizar(id, codigo, precio, fechaEntrega, idCotizacion);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        pedido.eliminar(id);
    }
}
