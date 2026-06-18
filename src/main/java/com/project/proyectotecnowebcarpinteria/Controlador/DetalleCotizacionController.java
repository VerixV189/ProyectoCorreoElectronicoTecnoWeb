package com.project.proyectotecnowebcarpinteria.Controlador;

import com.project.proyectotecnowebcarpinteria.Modelo.DetalleCotizacion;
import java.util.List;

/**
 * Controlador para la gestión de detalles de cotización.
 * Valida las entradas y delega las operaciones al modelo DetalleCotizacion.
 */
public class DetalleCotizacionController {

    private final DetalleCotizacion detalle;

    public DetalleCotizacionController() {
        this.detalle = new DetalleCotizacion();
    }

    /**
     * Lista los detalles de una cotización específica.
     *
     * @param idCotizacion ID de la cotización a consultar.
     * @return Lista de filas con los detalles.
     */
    public List<String[]> listar(int idCotizacion) {
        ValidacionEntrada.id(idCotizacion, "idCotizacion");
        return detalle.listar(idCotizacion);
    }

    /**
     * Registra un nuevo detalle de cotización.
     *
     * @param precio       Precio ofertado por el carpintero (debe ser positivo).
     * @param descripcion  Descripción del trabajo ofertado.
     * @param idCotizacion ID de la cotización a la que pertenece este detalle.
     * @param idCarpintero ID del carpintero que realiza la oferta.
     * @return ID generado del nuevo detalle.
     */
    public int registrar(float precio, String descripcion, int idCotizacion, int idCarpintero) {
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.id(idCotizacion, "idCotizacion");
        ValidacionEntrada.id(idCarpintero, "idCarpintero");
        return detalle.registrar(precio, descripcion, idCotizacion, idCarpintero);
    }

    /**
     * Actualiza un detalle de cotización existente.
     *
     * @param id           ID del detalle a actualizar.
     * @param precio       Nuevo precio.
     * @param descripcion  Nueva descripción.
     * @param idCotizacion ID de la cotización padre.
     * @param idCarpintero ID del carpintero asignado.
     */
    public void actualizar(int id, float precio, String descripcion, int idCotizacion, int idCarpintero) {
        ValidacionEntrada.id(id, "id");
        ValidacionEntrada.noNegativo(precio, "precio");
        ValidacionEntrada.texto(descripcion, "descripcion");
        ValidacionEntrada.id(idCotizacion, "idCotizacion");
        ValidacionEntrada.id(idCarpintero, "idCarpintero");
        detalle.actualizar(id, precio, descripcion, idCotizacion, idCarpintero);
    }

    /**
     * Elimina un detalle de cotización por su ID.
     *
     * @param id ID del detalle a eliminar.
     */
    public void eliminar(int id) {
        ValidacionEntrada.id(id, "id");
        detalle.eliminar(id);
    }
}
