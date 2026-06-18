package com.project.proyectotecnowebcarpinteria.Interfaz;

import com.project.proyectotecnowebcarpinteria.Controlador.VentaController;
import java.sql.Date;
import java.util.List;

/**
 * UI de correo para el recurso Venta.
 *
 * Comandos soportados:
 *
 *   LISVEN[*]
 *       → Lista todas las ventas registradas.
 *
 *   REGVEN[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]
 *       → Registra una venta de forma basica, SIN generar pagos automaticamente.
 *         Para generar pagos automaticos, use REGVENCON o REGVENCRE.
 *
 *   REGVENCON[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente]
 *       → Registra una venta al CONTADO con generacion automatica de pagos.
 *         Genera 1 pago por el total con estado 'Pagado' e interes 0.
 *
 *   REGVENCRE[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente,interes,numeroPagos]
 *       → Registra una venta a CREDITOREGVENCRE[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente,interes,numeroPagos] con generacion automatica de cuotas.
 *         Genera N pagos (minimo 2) con estado 'Pendiente', el interes indicado,
 *         y fechas de vencimiento cada 30 dias desde la fecha de entrega.
 *         Ejemplo: REGVENCRE[V-001,1500.00,2026-06-18,1,1,5.0,3]
 *           Genera 3 cuotas de 500 con 5% de interes:
 *             Cuota 1: vence 2026-07-18
 *             Cuota 2: vence 2026-08-17
 *             Cuota 3: vence 2026-09-16
 *
 *   ACTVEN[id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]
 *       → Actualiza los datos de una venta existente (sin modificar pagos).
 *
 *   ELIMVEN[id]
 *       → Elimina una venta por su ID (los pagos asociados tambien se eliminan).
 */
public class VentaUI implements ComandoUI {

    private final VentaController controller;

    public VentaUI() {
        this.controller = new VentaController();
    }

    @Override
    public String ejecutar(String comando, String parametros, java.util.List<String> imagenesAdjuntas) {
        try {
            switch (comando) {
                case "LISVEN":    return listar();
                case "REGVEN":    return registrar(parametros);
                case "REGVENCON": return registrarContado(parametros);
                case "REGVENCRE": return registrarCredito(parametros);
                case "ACTVEN":    return actualizar(parametros);
                case "ELIMVEN":   return eliminar(parametros);
                default: return error(comando, "Comando no asociado a VentaUI.");
            }
        } catch (Exception e) {
            return error(comando, e.getMessage());
        }
    }

    private String listar() {
        List<String[]> lista = controller.listar();
        if (lista == null || lista.isEmpty()) return "=== LISTA DE VENTAS ===\n(No hay ventas registradas.)";
        StringBuilder sb = new StringBuilder("=== LISTA DE VENTAS ===\n");
        sb.append(String.format("%-5s %-12s %-12s %-15s %-10s %-10s %-10s%n",
                "ID", "Codigo", "Total", "Fecha", "Tipo", "idPedido", "idCliente"));
        sb.append("-".repeat(80)).append("\n");
        for (String[] v : lista) {
            sb.append(String.format("%-5s %-12s %-12s %-15s %-10s %-10s %-10s%n",
                    get(v,0), get(v,1), get(v,2), get(v,3), get(v,4), get(v,5), get(v,6)));
        }
        return sb.toString();
    }

    /** REGVEN: registro basico sin pagos automaticos */
    private String registrar(String params) {
        String[] p = split(params, 6, "codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente");
        int id = controller.registrar(p[0], parseFloat(p[1], "totalCosto"), parseDate(p[2]), p[3],
                parseInt(p[4], "idPedido"), parseInt(p[5], "idCliente"));
        return "=== VENTA REGISTRADA ===\nID asignado: " + id
                + "\nNOTA: No se generaron pagos automaticos. Use REGVENCON o REGVENCRE para eso.";
    }

    /** REGVENCON: registro de venta al contado con 1 pago automatico */
    private String registrarContado(String params) {
        String[] p = split(params, 5, "codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente");
        int[] ids = controller.registrarContado(p[0], parseFloat(p[1], "totalCosto"), parseDate(p[2]),
                parseInt(p[3], "idPedido"), parseInt(p[4], "idCliente"));
        return "=== VENTA AL CONTADO REGISTRADA ===\n"
                + "ID Venta:  " + ids[0] + "\n"
                + "ID Pago:   " + ids[1] + "\n"
                + "Tipo:      Contado\n"
                + "Estado:    Pagado\n"
                + "Interes:   0\n"
                + "Se genero 1 pago automatico por el total de la venta.";
    }

    /** REGVENCRE: registro de venta a credito con N pagos automaticos */
    private String registrarCredito(String params) {
        String[] p = split(params, 7, "codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente,interes,numeroPagos");
        int numeroPagos = parseInt(p[6], "numeroPagos");
        float interes = parseFloat(p[5], "interes");

        int[] ids = controller.registrarCredito(p[0], parseFloat(p[1], "totalCosto"), parseDate(p[2]),
                parseInt(p[3], "idPedido"), parseInt(p[4], "idCliente"), interes, numeroPagos);

        StringBuilder sb = new StringBuilder("=== VENTA A CREDITO REGISTRADA ===\n");
        sb.append("ID Venta:      ").append(ids[0]).append("\n");
        sb.append("Tipo:          Credito\n");
        sb.append("Cuotas:        ").append(numeroPagos).append("\n");
        sb.append("Interes/cuota: ").append(interes).append("%\n");
        sb.append("IDs de pagos generados:\n");
        for (int i = 1; i < ids.length; i++) {
            sb.append("  Cuota ").append(i).append(": Pago ID ").append(ids[i]).append("\n");
        }
        sb.append("Vencimientos cada 30 dias desde la fecha de entrega.");
        return sb.toString();
    }

    private String actualizar(String params) {
        String[] p = split(params, 7, "id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente");
        controller.actualizar(parseInt(p[0], "id"), p[1], parseFloat(p[2], "totalCosto"), parseDate(p[3]),
                p[4], parseInt(p[5], "idPedido"), parseInt(p[6], "idCliente"));
        return "=== VENTA ACTUALIZADA ===\nID: " + p[0] + " actualizada correctamente.";
    }

    private String eliminar(String params) {
        int id = parseInt(params.trim(), "id");
        controller.eliminar(id);
        return "=== VENTA ELIMINADA ===\nID: " + id + " eliminada correctamente.";
    }

    private String[] split(String params, int expected, String formato) {
        String[] p = params.split("\u001F", -1);
        if (p.length != expected) throw new IllegalArgumentException("Se esperaban " + expected + " parametros: " + formato + "\nRecibidos: " + p.length);
        return p;
    }
    private int parseInt(String v, String c) { try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser entero. Recibido: \"" + v + "\""); } }
    private float parseFloat(String v, String c) { try { return Float.parseFloat(v.trim()); } catch (NumberFormatException e) { throw new IllegalArgumentException("Campo '" + c + "' debe ser numerico. Recibido: \"" + v + "\""); } }
    private Date parseDate(String v) { try { return Date.valueOf(v.trim()); } catch (Exception e) { throw new IllegalArgumentException("Fecha invalida. Formato: YYYY-MM-DD. Recibido: \"" + v + "\""); } }
    private String get(String[] arr, int i) { return (arr != null && i < arr.length && arr[i] != null) ? arr[i] : "-"; }

    private String error(String cmd, String motivo) {
        return "=== ERROR ===\nComando: " + cmd + "\nMotivo: " + motivo + "\n=============\n"
                + "Uso:\n"
                + "  LISVEN[*]\n"
                + "  REGVEN[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]\n"
                + "  REGVENCON[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente]\n"
                + "  REGVENCRE[codigo,totalCosto,fechaEntregado(YYYY-MM-DD),idPedido,idCliente,interes,numeroPagos]\n"
                + "  ACTVEN[id,codigo,totalCosto,fechaEntregado(YYYY-MM-DD),tipo,idPedido,idCliente]\n"
                + "  ELIMVEN[id]";
    }
}
