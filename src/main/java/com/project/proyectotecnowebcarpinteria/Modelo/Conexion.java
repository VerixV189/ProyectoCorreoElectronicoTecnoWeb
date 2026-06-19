/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author erik
 */
public class Conexion {
    public static final String URL = "jdbc:postgresql://www.tecnoweb.org.bo:5432/db_grupo07sa";
    public static final String USER = "grupo07sa";
    public static final String PASSWORD = "grup007grup007*";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

