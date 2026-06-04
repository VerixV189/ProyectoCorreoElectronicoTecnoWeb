/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Rol;
import java.util.List;

/**
 *
 * @author erik
 */
public class RolController {
    Rol rol;

    public RolController() {
        rol = new Rol();
    }

    public List<String[]> listar() {
        return rol.listar();
    }

    public int registrar(String nombre, String estado) {
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.texto(estado, "estado");
        return rol.registrar(nombre, estado);
    }

    public void actualizar(int id, String nombre, String estado) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.texto(estado, "estado");
        rol.actualizar(id, nombre, estado);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        rol.eliminar(id);
    }
}
