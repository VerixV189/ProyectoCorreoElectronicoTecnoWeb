/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Tipo;
import java.util.List;

/**
 *
 * @author erik
 */
public class TipoController {
    Tipo tipo;

    public TipoController() {
        tipo = new Tipo();
    }

    public List<String[]> listar() {
        return tipo.listar();
    }

    public int registrar(String nombre, String descripcion, String estado) {
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.texto(estado, "estado");
        return tipo.registrar(nombre, descripcion, estado);
    }

    public void actualizar(int id, String nombre, String descripcion, String estado) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(nombre, "nombre");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.texto(estado, "estado");
        tipo.actualizar(id, nombre, descripcion, estado);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        tipo.eliminar(id);
    }
}
