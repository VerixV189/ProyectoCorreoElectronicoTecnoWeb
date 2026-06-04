/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Proveedor;
import java.util.List;

/**
 *
 * @author erik
 */
public class ProveedorController {
    Proveedor proveedor;

    public ProveedorController() {
        proveedor = new Proveedor();
    }

    public List<String[]> listar() {
        return proveedor.listar();
    }

    public int registrar(String nombreEmpresa, String telefono, String direccion) {
        ValidacionEntrada.texto(nombreEmpresa, "nombreEmpresa");
        ValidacionEntrada.texto(telefono, "telefono");
        ValidacionEntrada.texto(direccion, "direccion");
        return proveedor.registrar(nombreEmpresa, telefono, direccion);
    }

    public void actualizar(int id, String nombreEmpresa, String telefono, String direccion) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(nombreEmpresa, "nombreEmpresa");
        ValidacionEntrada.texto(telefono, "telefono");
        ValidacionEntrada.texto(direccion, "direccion");
        proveedor.actualizar(id, nombreEmpresa, telefono, direccion);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        proveedor.eliminar(id);
    }
}
