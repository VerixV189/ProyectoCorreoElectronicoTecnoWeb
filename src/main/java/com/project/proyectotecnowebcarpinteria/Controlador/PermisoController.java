/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Permiso;
import java.util.List;

/**
 *
 * @author erik
 */
public class PermisoController {
    Permiso permiso;

    public PermisoController() {
        permiso = new Permiso();
    }

    public List<String[]> listar() {
        return permiso.listar();
    }

    public int registrar(String nombre) {
        ValidacionEntrada.texto(nombre, "nombre");
        return permiso.registrar(nombre);
    }

    public void actualizar(int id, String nombre) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(nombre, "nombre");
        permiso.actualizar(id, nombre);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        permiso.eliminar(id);
    }
}
