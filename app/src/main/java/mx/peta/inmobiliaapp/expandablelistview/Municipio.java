package mx.peta.inmobiliaapp.expandablelistview;

/**
 * Created by rayo on 11/2/16.
 */

public class Municipio implements Comparable<Municipio> {
    private String nombreMunicipio;
    private String claveMunicipio;

    public Municipio(String nombre, String clave) {
        this.nombreMunicipio = nombre;
        this.claveMunicipio  = clave;
    }

    public String getNombreMunicipio() {return nombreMunicipio; }

    public String getClaveMunicipio() { return claveMunicipio; }

    @Override
    public int compareTo(Municipio o) {
        /* Este método no ordena correctamente las vocales acentuadas,
           la forma de arreglarlo sería usar replace como se muestra
           sFernando = sFernando.replace('e','a').replace('i','a')
               .replace('o','a').replace('u','a');
         */
        return nombreMunicipio.compareTo(o.getNombreMunicipio());
    }
}
