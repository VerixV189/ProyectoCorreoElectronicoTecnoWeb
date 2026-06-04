/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import java.sql.Date;

/**
 *
 * @author erik
 */
public final class ValidacionEntrada {

    private ValidacionEntrada() {
    }

    public static void texto(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " no puede ser nulo ni vacío.");
        }
    }

    public static void id(int valor, String nombreCampo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(nombreCampo + " debe ser mayor que cero.");
        }
    }

    public static void noNegativo(int valor, String nombreCampo) {
        if (valor < 0) {
            throw new IllegalArgumentException(nombreCampo + " no puede ser negativo.");
        }
    }

    public static void noNegativo(float valor, String nombreCampo) {
        if (valor < 0) {
            throw new IllegalArgumentException(nombreCampo + " no puede ser negativo.");
        }
    }

    public static void positivo(int valor, String nombreCampo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(nombreCampo + " debe ser mayor que cero.");
        }
    }

    public static void fecha(Date valor, String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException(nombreCampo + " no puede ser nulo.");
        }
    }

    public static void datosUsuario(String nombre, String apellido, String email, String password,
            String telefono, String estado, int idRol) {
        texto(nombre, "nombre");
        texto(apellido, "apellido");
        texto(email, "email");
        texto(password, "password");
        texto(telefono, "telefono");
        texto(estado, "estado");
        id(idRol, "idRol");
    }
}
