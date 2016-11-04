package mx.peta.inmobiliaapp;

/**
 * Created by rayo on 11/3/16.
 */
public class EstadoGeneral {
    private static EstadoGeneral ourInstance = new EstadoGeneral();

    public static EstadoGeneral getInstance() {
        return ourInstance;
    }

    private EstadoGeneral() {
    }

    /*
        En esta clase se almacenan las variable globales que se requieran para controlar al sistema
     */
    int avaluosComprados;
    public int getAvaluosComprados() { return avaluosComprados; }
    public void setAvaluosComprados() { this.avaluosComprados = avaluosComprados; }
}
