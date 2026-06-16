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

    public int registrar(String url, int idProducto, int idInsumo) {
        ValidacionEntrada.texto(url, "url");
        if (idProducto > 0) {
            ValidacionEntrada.id(idProducto, "idProducto");
        }
        if (idInsumo > 0) {
            ValidacionEntrada.id(idInsumo, "idInsumo");
        }
        return imagen.registrar(url, idProducto, idInsumo);
    }

    public void actualizar(int id, String url, int idProducto, int idInsumo) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(url, "url");
        if (idProducto > 0) {
            ValidacionEntrada.id(idProducto, "idProducto");
        }
        if (idInsumo > 0) {
            ValidacionEntrada.id(idInsumo, "idInsumo");
        }
        imagen.actualizar(id, url, idProducto, idInsumo);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        imagen.eliminar(id);
    }
}
