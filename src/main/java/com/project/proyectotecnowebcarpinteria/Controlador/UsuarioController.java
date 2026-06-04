/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Usuario;
import java.util.List;

/**
 *
 * @author erik
 */
public class UsuarioController {
    Usuario usuario;

    public UsuarioController() {
        usuario = new Usuario();
    }

    public List<String[]> listar() {
        return usuario.listar();
    }

    public int registrar(String nombre, String apellido, String email, String password, String telefono, String estado, int idRol) {
        ValidacionEntrada.datosUsuario(nombre, apellido, email, password, telefono, estado, idRol);
        return usuario.registrar(nombre, apellido, email, password, telefono, estado, idRol);
    }

    public void actualizar(int id, String nombre, String apellido, String email, String password, String telefono, String estado, int idRol) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.datosUsuario(nombre, apellido, email, password, telefono, estado, idRol);
        usuario.actualizar(id, nombre, apellido, email, password, telefono, estado, idRol);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        usuario.eliminar(id);
    }
}
