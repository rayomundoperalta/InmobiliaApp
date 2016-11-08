package mx.peta.inmobiliaapp;

/**
 * Created by rayo on 11/3/16.
 */
public class Propiedad {
    private static Propiedad ourInstance = new Propiedad();

    public static Propiedad getInstance() {
        return ourInstance;
    }

    private Propiedad() {
    }

    /*
        Estas son las variables que definen una propiedad, son usadas para pasarselas al
        WEB Service de valuación.
     */

    private Double tipologia = 0.0;
    public Double getTipologia() { return this.tipologia; }
    public void setTipologia(Double tipologia) {this.tipologia = tipologia; }

    private Double CP = 0.0;
    public Double getCP() { return this.CP; }
    public void setCP(Double CP) { this.CP = CP; }

    private Double delegacion = 0.0;
    public Double getDelegacion() { return this.delegacion; }
    public void setDelegacion(Double delegacion) { this.delegacion = delegacion; }

    private Double entidad = 0.0;
    public Double getEntidad() { return this.entidad; }
    public void setEntidad(Double entidad) { this.entidad = entidad; }

    private Double proximidadUrbana = 0.0;
    public Double getProximidadUrbana() { return this.proximidadUrbana; }
    public void setProximidadUrbana(Double proximidadUrbana) { this.proximidadUrbana = proximidadUrbana; }

    private Double claseInmueble = 0.0;
    public Double getClaseInmueble() { return this.claseInmueble; }
    public void setClaseInmueble(Double claseInmueble) { this.claseInmueble = claseInmueble; }

    private Double vidaUtil = 0.0;
    public Double getVidaUtil() { return this.vidaUtil; }
    public void setVidaUtil(Double vidaUtil) { this.vidaUtil = vidaUtil; }

    private Double superTerreno = 0.0;
    public Double getSuperTerreno() { return this.superTerreno; }
    public void setSuperTerreno(Double superTerreno) { this.superTerreno = superTerreno; }

    private Double superConstruido = 0.0;
    public Double getSuperConstruido() { return this.superConstruido; }
    public void setSuperConstruido(Double superConstruido) { this.superConstruido = superConstruido; }

    private Double valConst = 0.0;
    public Double getValConst() { return this.valConst; }
    public void setValConst(Double valConst) { this.valConst = valConst; }

    private Double valConcluido = 0.0;
    public Double getValConcluido() { return this.valConcluido; }
    public void setValConcluido(Double valConcluido) { this.valConcluido = valConcluido; }

    private Double revisadoManualment = 0.0;
    public Double getRevisadoManualment() { return this.revisadoManualment; }
    public void set(Double revisadoManualment) { this.revisadoManualment = revisadoManualment; }

    private Double sensibilidad = 0.0;
    public Double getSensibilidad() { return this.sensibilidad; }
    public void setSensibilidad(Double sensibilidad) { this.sensibilidad = sensibilidad; }
}
/*
    Estos aon los tipos e datos que se usan para llamar al web service
    @Query("tipologia") Double tipologia,
    @Query("CP") Double CP,
    @Query("delegacion") Double delegacion,
    @Query("entidad") Double entidad,
    @Query("proximidadUrbana") Double proximidadUrbana,
    @Query("claseInmueble") Double claseInmueble,
    @Query("vidautil") Double vidaUtil,
    @Query("superTerreno") Double superTerreno,
    @Query("superConstruido") Double superConstruido,
    @Query("valConst") Double valConst,
    @Query("valConcluido") Double valConcluido,
    @Query("revisadoManualmente") int revisadoManualment,
    @Query("USER") String USER,
    @Query("PASSWORD") String PASSWORD,
    @Query("sensibilidad") Double sensibilidad
 */