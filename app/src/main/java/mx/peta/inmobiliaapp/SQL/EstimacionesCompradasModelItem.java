package mx.peta.inmobiliaapp.SQL;

/**
 * Created by rayo on 11/25/16.
 */

public class EstimacionesCompradasModelItem {
    public int id;
    public String fechaCompra;
    public String payKey;

    public EstimacionesCompradasModelItem(String fechaCompra, String payKey) {
        this.fechaCompra      = fechaCompra;
        this.payKey           = payKey;
    }
}
