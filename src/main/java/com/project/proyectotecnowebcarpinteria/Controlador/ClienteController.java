/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Cliente;
import com.project.proyectotecnowebcarpinteria.Modelo.Usuario;
import java.util.List;

/**
 *
 * @author erik
 */
public class ClienteController {
    Usuario usuario;
    Cliente cliente;

    public ClienteController() {
        this.usuario = new Usuario();
        this.cliente = new Cliente();
    }

    public List<String[]> listar() {
        return cliente.listar();
    }

    public int registrar(String nombre, String apellido, String email, String password, String telefono, String estado, String nitFacturacion, String razonSocial, String direccionEnvio, int idRol) {
        ValidacionEntrada.datosUsuario(nombre, apellido, email, password, telefono, estado, idRol);
        ValidacionEntrada.texto(nitFacturacion, "nitFacturacion");
        ValidacionEntrada.texto(razonSocial, "razonSocial");
        ValidacionEntrada.texto(direccionEnvio, "direccionEnvio");
        int id = usuario.registrar(nombre, apellido, email, password, telefono, estado, idRol);
        return cliente.registrar(id, nitFacturacion, razonSocial, direccionEnvio);
    }

    public void actualizar(int id, String nombre, String apellido, String email, String password, String telefono, String estado, String nitFacturacion, String razonSocial, String direccionEnvio, int idRol) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.datosUsuario(nombre, apellido, email, password, telefono, estado, idRol);
        ValidacionEntrada.texto(nitFacturacion, "nitFacturacion");
        ValidacionEntrada.texto(razonSocial, "razonSocial");
        ValidacionEntrada.texto(direccionEnvio, "direccionEnvio");
        usuario.actualizar(id, nombre, apellido, email, password, telefono, estado, idRol);
        cliente.actualizar(id, nitFacturacion, razonSocial, direccionEnvio);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        usuario.eliminar(id);
        cliente.eliminar(id);
    }
}
