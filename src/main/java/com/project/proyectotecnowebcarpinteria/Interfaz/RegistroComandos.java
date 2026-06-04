package com.project.proyectotecnowebcarpinteria.Interfaz;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registro central de comandos del sistema de carpintería.
 *
 * Asocia cada prefijo de comando (en MAYÚSCULAS) con la instancia de
 * {@link ComandoUI} que sabe ejecutarlo.
 *
 * Para agregar un nuevo recurso:
 *   1. Crea la clase XxxUI implementando ComandoUI.
 *   2. Registra sus comandos en el bloque estático de abajo.
 */
public class RegistroComandos {

    /** Mapa inmutable: nombreComando → instancia de ComandoUI. */
    private static final Map<String, ComandoUI> REGISTRO;

    static {
        Map<String, ComandoUI> mapa = new HashMap<>();

        // --- Usuario ---
        UsuarioUI usuarioUI = new UsuarioUI();
        mapa.put("LISUSU",  usuarioUI);
        mapa.put("REGUSU",  usuarioUI);
        mapa.put("ACTUSU",  usuarioUI);
        mapa.put("ELIMUSU", usuarioUI);

        // --- Cliente ---
        ClienteUI clienteUI = new ClienteUI();
        mapa.put("LISCLI",  clienteUI);
        mapa.put("REGCLI",  clienteUI);
        mapa.put("ACTCLI",  clienteUI);
        mapa.put("ELIMCLI", clienteUI);

        // --- Carpintero ---
        CarpinteroUI carpinteroUI = new CarpinteroUI();
        mapa.put("LISCARP",  carpinteroUI);
        mapa.put("REGCARP",  carpinteroUI);
        mapa.put("ACTCARP",  carpinteroUI);
        mapa.put("ELIMCARP", carpinteroUI);

        // --- Producto ---
        ProductoUI productoUI = new ProductoUI();
        mapa.put("LISPROD",  productoUI);
        mapa.put("REGPROD",  productoUI);
        mapa.put("ACTPROD",  productoUI);
        mapa.put("ELIMPROD", productoUI);

        // --- Cotizacion ---
        CotizacionUI cotizacionUI = new CotizacionUI();
        mapa.put("LISCOT",  cotizacionUI);
        mapa.put("REGCOT",  cotizacionUI);
        mapa.put("ACTCOT",  cotizacionUI);
        mapa.put("ELIMCOT", cotizacionUI);

        // --- Pedido ---
        PedidoUI pedidoUI = new PedidoUI();
        mapa.put("LISPED",  pedidoUI);
        mapa.put("REGPED",  pedidoUI);
        mapa.put("ACTPED",  pedidoUI);
        mapa.put("ELIMPED", pedidoUI);

        // --- DetallePedido ---
        DetallePedidoUI detallePedidoUI = new DetallePedidoUI();
        mapa.put("LISDET",  detallePedidoUI);
        mapa.put("REGDET",  detallePedidoUI);
        mapa.put("ACTDET",  detallePedidoUI);
        mapa.put("ELIMDET", detallePedidoUI);

        // --- Pago ---
        PagoUI pagoUI = new PagoUI();
        mapa.put("LISPAG",  pagoUI);
        mapa.put("REGPAG",  pagoUI);
        mapa.put("ACTPAG",  pagoUI);
        mapa.put("ELIMPAG", pagoUI);

        // --- Venta ---
        VentaUI ventaUI = new VentaUI();
        mapa.put("LISVEN",  ventaUI);
        mapa.put("REGVEN",  ventaUI);
        mapa.put("ACTVEN",  ventaUI);
        mapa.put("ELIMVEN", ventaUI);

        // --- Insumo ---
        InsumoUI insumoUI = new InsumoUI();
        mapa.put("LISINSU",  insumoUI);
        mapa.put("REGINSU",  insumoUI);
        mapa.put("ACTINSU",  insumoUI);
        mapa.put("ELIMINSU", insumoUI);

        // --- Proveedor ---
        ProveedorUI proveedorUI = new ProveedorUI();
        mapa.put("LISPROV",  proveedorUI);
        mapa.put("REGPROV",  proveedorUI);
        mapa.put("ACTPROV",  proveedorUI);
        mapa.put("ELIMPROV", proveedorUI);

        // --- Rol ---
        RolUI rolUI = new RolUI();
        mapa.put("LISROL",  rolUI);
        mapa.put("REGROL",  rolUI);
        mapa.put("ACTROL",  rolUI);
        mapa.put("ELIMROL", rolUI);

        // --- Tipo ---
        TipoUI tipoUI = new TipoUI();
        mapa.put("LISTIP",  tipoUI);
        mapa.put("REGTIP",  tipoUI);
        mapa.put("ACTTIP",  tipoUI);
        mapa.put("ELIMTIP", tipoUI);

        // --- Imagen ---
        ImagenUI imagenUI = new ImagenUI();
        mapa.put("LISIMG",  imagenUI);
        mapa.put("REGIMG",  imagenUI);
        mapa.put("ACTIMG",  imagenUI);
        mapa.put("ELIMIMG", imagenUI);

        // --- Permiso ---
        PermisoUI permisoUI = new PermisoUI();
        mapa.put("LISPERM",  permisoUI);
        mapa.put("REGPERM",  permisoUI);
        mapa.put("ACTPERM",  permisoUI);
        mapa.put("ELIMPERM", permisoUI);

        REGISTRO = Collections.unmodifiableMap(mapa);
    }

    /**
     * Devuelve la instancia de {@link ComandoUI} que maneja el comando indicado,
     * o {@code null} si el comando no está registrado.
     *
     * @param nombreComando Nombre del comando en MAYÚSCULAS (ej: "LISUSU").
     * @return La implementación de ComandoUI correspondiente, o null.
     */
    public static ComandoUI obtener(String nombreComando) {
        return REGISTRO.get(nombreComando);
    }

    /**
     * Genera un texto de ayuda con todos los comandos disponibles,
     * agrupados por recurso. Útil para incluir en los correos de error.
     *
     * @return String con la lista de comandos disponibles.
     */
    public static String listarComandosDisponibles() {
        return "Comandos disponibles (deben enviarse en MAYÚSCULAS):\n"
                + "\n[USUARIO]\n"
                + "  LISUSU[*]\n"
                + "  REGUSU[nombre,apellido,email,password,telefono,estado,idRol]\n"
                + "  ACTUSU[id,nombre,apellido,email,password,telefono,estado,idRol]\n"
                + "  ELIMUSU[id]\n"
                + "\n[CLIENTE]\n"
                + "  LISCLI[*]\n"
                + "  REGCLI[nombre,apellido,email,password,telefono,estado,nitFacturacion,razonSocial,direccionEnvio,idRol]\n"
                + "  ACTCLI[id,nombre,apellido,email,password,telefono,estado,nitFacturacion,razonSocial,direccionEnvio,idRol]\n"
                + "  ELIMCLI[id]\n"
                + "\n[CARPINTERO]\n"
                + "  LISCARP[*]\n"
                + "  REGCARP[nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol]\n"
                + "  ACTCARP[id,nombre,apellido,email,password,telefono,estado,especialidad,costoHora,idRol]\n"
                + "  ELIMCARP[id]\n"
                + "\n[PRODUCTO]\n"
                + "  LISPROD[*]\n"
                + "  REGPROD[nombre,cantidad,precio,descripcion,estado,idTipo]\n"
                + "  ACTPROD[id,nombre,cantidad,precio,descripcion,estado,idTipo]\n"
                + "  ELIMPROD[id]\n"
                + "\n[COTIZACIÓN]\n"
                + "  LISCOT[*]\n"
                + "  REGCOT[descripcion,estado,idCliente,idCarpintero]\n"
                + "  ACTCOT[id,descripcion,estado,idCliente,idCarpintero]\n"
                + "  ELIMCOT[id]\n"
                + "\n[PEDIDO]\n"
                + "  LISPED[*]\n"
                + "  REGPED[codigo,precio,fechaEntrega(YYYY-MM-DD),idCotizacion]\n"
                + "  ACTPED[id,codigo,precio,fechaEntrega(YYYY-MM-DD),idCotizacion]\n"
                + "  ELIMPED[id]\n"
                + "\n[DETALLE PEDIDO]\n"
                + "  LISDET[idPedido]\n"
                + "  REGDET[cantidad,precio,estado,descripcion,idPedido,idProducto]\n"
                + "  ACTDET[id,cantidad,precio,estado,descripcion,idPedido,idProducto]\n"
                + "  ELIMDET[id]\n"
                + "\n[PAGO]\n"
                + "  LISPAG[*]\n"
                + "  REGPAG[subtotal,interes,idVenta]\n"
                + "  ACTPAG[id,subtotal,interes,idVenta]\n"
                + "  ELIMPAG[id]\n"
                + "\n[VENTA]\n"
                + "  LISVEN[*]\n"
                + "  REGVEN[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]\n"
                + "  ACTVEN[id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]\n"
                + "  ELIMVEN[id]\n"
                + "\n[INSUMO]\n"
                + "  LISINSU[*]\n"
                + "  REGINSU[nombre,imagen,idProveedor]\n"
                + "  ACTINSU[id,nombre,imagen,idProveedor]\n"
                + "  ELIMINSU[id]\n"
                + "\n[PROVEEDOR]\n"
                + "  LISPROV[*]\n"
                + "  REGPROV[nombreEmpresa,telefono,direccion]\n"
                + "  ACTPROV[id,nombreEmpresa,telefono,direccion]\n"
                + "  ELIMPROV[id]\n"
                + "\n[ROL]\n"
                + "  LISROL[*]\n"
                + "  REGROL[nombre,estado]\n"
                + "  ACTROL[id,nombre,estado]\n"
                + "  ELIMROL[id]\n"
                + "\n[TIPO]\n"
                + "  LISTIP[*]\n"
                + "  REGTIP[nombre,descripcion,estado]\n"
                + "  ACTTIP[id,nombre,descripcion,estado]\n"
                + "  ELIMTIP[id]\n"
                + "\n[IMAGEN]\n"
                + "  LISIMG[*]\n"
                + "  REGIMG[url,idProducto]\n"
                + "  ACTIMG[id,url,idProducto]\n"
                + "  ELIMIMG[id]\n"
                + "\n[PERMISO]\n"
                + "  LISPERM[*]\n"
                + "  REGPERM[nombre]\n"
                + "  ACTPERM[id,nombre]\n"
                + "  ELIMPERM[id]";
    }

    // Constructor privado: clase de utilidad, no instanciable.
    private RegistroComandos() {}
}
