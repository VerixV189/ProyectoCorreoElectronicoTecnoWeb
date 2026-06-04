/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Cotizacion;
import java.util.List;

/**
 *
 * @author erik
 */
public class CotizacionController {
    Cotizacion cotizacion;

    public CotizacionController() {
        cotizacion = new Cotizacion();
    }

    public List<String[]> listar() {
        return cotizacion.listar();
    }

    public int registrar(String descripcion, String estado, int idCliente, int idCarpintero) {
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.id(idCliente, "idCliente");
        ValidacionEntrada.id(idCarpintero, "idCarpintero");
        return cotizacion.registrar(descripcion, estado, idCliente, idCarpintero);
    }

    public void actualizar(int id, String descripcion, String estado, int idCliente, int idCarpintero) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.texto(estado, "estado");
        ValidacionEntrada.id(idCliente, "idCliente");
        ValidacionEntrada.id(idCarpintero, "idCarpintero");
        cotizacion.actualizar(id, descripcion, estado, idCliente, idCarpintero);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        cotizacion.eliminar(id);
    }
}
