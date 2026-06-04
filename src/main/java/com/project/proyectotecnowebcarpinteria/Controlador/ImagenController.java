/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Imagen;
import java.util.List;

/**
 *
 * @author erik
 */
public class ImagenController {
    Imagen imagen;

    public ImagenController() {
        imagen = new Imagen();
    }

    public List<String[]> listar() {
        return imagen.listar();
    }

    public int registrar(String url, int idProducto) {
        ValidacionEntrada.texto(url, "url");
        ValidacionEntrada.id(idProducto, "idProducto");
        return imagen.registrar(url, idProducto);
    }

    public void actualizar(int id, String url, int idProducto) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(url, "url");
        ValidacionEntrada.id(idProducto, "idProducto");
        imagen.actualizar(id, url, idProducto);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        imagen.eliminar(id);
    }
}
