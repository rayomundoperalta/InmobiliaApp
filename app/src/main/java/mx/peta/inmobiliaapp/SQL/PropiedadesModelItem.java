package mx.peta.inmobiliaapp.SQL;

/**
 * Created by rayo on 11/9/16.
 */

/*

 */

public class PropiedadesModelItem {

    public int id;
    public String telefono;
    public String direccion;
    public double latitud;
    public double longitud;
    public String photoFileName;
    public Double tipologia;
    public Double CP;
    public Double delegacion;
    public Double entidad;
    public Double proximidadUrbana;
    public Double claseInmueble;
    public Double vidaUtil;
    public Double superTerreno;
    public Double superConstruido;
    public Double valConst;
    public Double valConcluido;
    public Double valEstimado;
    public Double valDesStn;
    public int revisadoManualment;
    public Double sensibilidad;

    public PropiedadesModelItem(
            String telefono,
            String direccion,
            double latitud,
            double longitud,
            String photoFileName,
            Double tipologia,
            Double CP,
            Double delegacion,
            Double entidad,
            Double proximidadUrbana,
            Double claseInmueble,
            Double vidaUtil,
            Double superTerreno,
            Double superConstruido,
            Double valConst,
            Double valConcluido,
            Double valEstimado,
            Double valDesStn,
            int revisadoManualment,
            Double sensibilidad) {
        this.telefono           = telefono;
        this.direccion          = direccion;
        this.latitud            = latitud;
        this.longitud           = longitud;
        this.photoFileName      = photoFileName;
        this.tipologia          = tipologia;
        this.CP                 = CP;
        this.delegacion         = delegacion;
        this.entidad            = entidad;
        this.proximidadUrbana   = proximidadUrbana;
        this.claseInmueble      = claseInmueble;
        this.vidaUtil           = vidaUtil;
        this.superTerreno       = superTerreno;
        this.superConstruido    = superConstruido;
        this.valConst           = valConst;
        this.valConcluido       = valConcluido;
        this.valEstimado        = valEstimado;
        this.valDesStn          = valDesStn;
        this.revisadoManualment = revisadoManualment;
        this.sensibilidad       = sensibilidad;
    }
}
