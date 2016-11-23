package mx.peta.inmobiliaapp;

/**
 * Created by rayo on 11/23/16.
 */

public class ValorEstimado {
    public static double porcentaje(double valor, double desStn) {
        if ((valor == 0.0) || (desStn == 0.0)) {
            return 0.0;
        } else {
            return (100.0 * desStn) / valor;
        }
    }
}
