/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Carpintero;
import com.project.proyectotecnowebcarpinteria.Modelo.Usuario;
import java.util.List;

/**
 *
 * @author erik
 */
public class CarpinteroController {
    Usuario usuario;
    Carpintero carpintero;

    public CarpinteroController() {
        this.usuario = new Usuario();
        this.carpintero = new Carpintero();
    }

    public List<String[]> listar() {
        return carpintero.listar();
    }

    public int registrar(String nombre, String apellido, String email, String password, String telefono, String estado, String especialidad, float costoHora, int idRol) {
        ValidacionEntrada.datosUsuario(nombre, apellido, email, password, telefono, estado, idRol);
        ValidacionEntrada.texto(especialidad, "especialidad");
        ValidacionEntrada.noNegativo(costoHora, "costoHora");
        int id = usuario.registrar(nombre, apellido, email, password, telefono, estado, idRol);
        return carpintero.registrar(id, especialidad, costoHora);
    }

    public void actualizar(int id, String nombre, String apellido, String email, String password, String telefono, String estado, String especialidad, float costoHora, int idRol) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.datosUsuario(nombre, apellido, email, password, telefono, estado, idRol);
        ValidacionEntrada.texto(especialidad, "especialidad");
        ValidacionEntrada.noNegativo(costoHora, "costoHora");
        usuario.actualizar(id, nombre, apellido, email, password, telefono, estado, idRol);
        carpintero.actualizar(id, especialidad, costoHora);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        usuario.eliminar(id);
        carpintero.eliminar(id);
    }
}
