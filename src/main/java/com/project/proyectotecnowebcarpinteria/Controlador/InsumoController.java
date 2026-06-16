/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Insumo;
import java.util.List;

/**
 *
 * @author erik
 */
public class InsumoController {
    Insumo insumo;

    public InsumoController() {
        insumo = new Insumo();
    }

    public List<String[]> listar() {
        return insumo.listar();
    }

    public int registrar(String nombre, int idProveedor) {
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.id(idProveedor, "idProveedor");
        return insumo.registrar(nombre, idProveedor);
    }

    public void actualizar(int id, String nombre, int idProveedor) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.id(idProveedor, "idProveedor");
        insumo.actualizar(id, nombre, idProveedor);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        insumo.eliminar(id);
    }
}
