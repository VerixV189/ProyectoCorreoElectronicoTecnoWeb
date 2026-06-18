/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.Cotizacion;
import com.project.proyectotecnowebcarpinteria.Modelo.Pedido;
import java.sql.Date;
import java.util.List;

/**
 * Controlador para la gestión de pedidos.
 *
 * Al registrar un pedido (REGPED), este controlador:
 *   1. Verifica que la cotización asociada NO esté en estado 'Cancelada' o 'Rechazada'.
 *   2. Inserta el pedido en la base de datos.
 *   3. Actualiza automáticamente el estado de la cotización a 'Confirmada'.
 */
public class PedidoController {
    Pedido pedido;
    Cotizacion cotizacion;

    public PedidoController() {
        pedido = new Pedido();
        cotizacion = new Cotizacion();
    }

    public List<String[]> listar() {
        return pedido.listar();
    }

    /**
     * Registra un nuevo pedido verificando el estado de la cotización.
     *
     * @param codigo        Código único del pedido.
     * @param precio        Precio total del pedido.
     * @param fechaEntrega  Fecha estimada de entrega (YYYY-MM-DD).
     * @param idCotizacion  ID de la cotización confirmada que origina este pedido.
     * @return ID generado del pedido.
     * @throws IllegalArgumentException si la cotización está Cancelada, Rechazada o no existe.
     */
    public int registrar(String codigo, float precio, Date fechaEntrega, int idCotizacion) {
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.fecha(fechaEntrega, "fechaEntrega");
        ValidacionEntrada.id(idCotizacion, "idCotizacion");

        // 1. Verificar estado de la cotización antes de crear el pedido
        String estadoCot = cotizacion.obtenerEstado(idCotizacion);
        if (estadoCot == null) {
            throw new IllegalArgumentException(
                    "La cotización con ID " + idCotizacion + " no existe.");
        }
        String estadoUpper = estadoCot.trim().toLowerCase();
        if (estadoUpper.equals("cancelada") || estadoUpper.equals("rechazada")) {
            throw new IllegalArgumentException(
                    "No se puede crear un pedido sobre la cotización #" + idCotizacion
                    + " porque su estado es '" + estadoCot + "'.\n"
                    + "Solo se permiten cotizaciones que no estén Canceladas o Rechazadas.");
        }

        // 2. Registrar el pedido
        int idPedido = pedido.registrar(codigo, precio, fechaEntrega, idCotizacion);

        // 3. Actualizar el estado de la cotización a 'Confirmada'
        if (idPedido > 0) {
            cotizacion.actualizarEstado(idCotizacion, "Confirmada");
        }

        return idPedido;
    }

    public void actualizar(int id, String codigo, float precio, Date fechaEntrega, int idCotizacion) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.texto(codigo, "codigo");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.fecha(fechaEntrega, "fechaEntrega");
        ValidacionEntrada.id(idCotizacion, "idCotizacion");
        pedido.actualizar(id, codigo, precio, fechaEntrega, idCotizacion);
    }

    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        pedido.eliminar(id);
    }
}

