/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.DetallePedido;
import java.util.List;

/**
 *
 * @author erik
 */
public class DetallePedidoController {
    DetallePedido detallePedido;

    public DetallePedidoController() {
        detallePedido = new DetallePedido();
    }

    public List<String[]> listar(int idPedido) {
        ValidacionEntrada.id(idPedido, "idPedido");
        return detallePedido.listar(idPedido);
    }

    public int registrar(int cantidad, float precio, String estado, String descripcion, int idPedido, int idProducto) {
        ValidacionEntrada.positivo(cantidad, "cantidad");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.id(idPedido, "idPedido");
        if (idProducto > 0) {
            ValidacionEntrada.id(idProducto, "idProducto");
        }
        return detallePedido.registrar(cantidad, precio, estado, descripcion, idPedido, idProducto);
    }

    public void actualizar(int id, int cantidad, float precio, String estado, String descripcion, int idPedido, int idProducto) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.positivo(cantidad, "cantidad");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.id(idPedido, "idPedido");
        if (idProducto > 0) {
            ValidacionEntrada.id(idProducto, "idProducto");
        }
        detallePedido.actualizar(id, cantidad, precio, estado, descripcion, idPedido, idProducto);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        detallePedido.eliminar(id);
    }
}
