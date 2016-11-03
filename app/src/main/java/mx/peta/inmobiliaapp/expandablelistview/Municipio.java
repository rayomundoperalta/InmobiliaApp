package mx.peta.inmobiliaapp.expandablelistview;

/**
 * Created by rayo on 11/2/16.
 */

public class Municipio {
    private String nombreMunicipio;
    private String claveMunicipio;

    public Municipio(String nombre, String clave) {
        this.nombreMunicipio = nombre;
        this.claveMunicipio  = clave;
    }

    public String getNombreMunicipio() {return nombreMunicipio; }

    public String getClaveMunicipio() { return claveMunicipio; }
}
