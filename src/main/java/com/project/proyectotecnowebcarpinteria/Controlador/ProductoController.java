/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Producto;
import java.util.List;

/**
 *
 * @author erik
 */
public class ProductoController {
    Producto producto;

    public ProductoController() {
        producto = new Producto();
    }

    public List<String[]> listar() {
        return producto.listar();
    }

    public int registrar(String nombre, int cantidad, float precio, String descripcion, String estado, int idTipo) {
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.noNegativo(cantidad, "cantidad");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.id(idTipo, "idTipo");
        return producto.registrar(nombre, cantidad, precio, descripcion, estado, idTipo);
    }

    public void actualizar(int id, String nombre, int cantidad, float precio, String descripcion, String estado, int idTipo) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.noNegativo(cantidad, "cantidad");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.id(idTipo, "idTipo");
        producto.actualizar(id, nombre, cantidad, precio, descripcion, estado, idTipo);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        producto.eliminar(id);
    }
}
