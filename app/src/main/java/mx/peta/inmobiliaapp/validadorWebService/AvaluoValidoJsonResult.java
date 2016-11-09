package mx.peta.inmobiliaapp.validadorWebService;

/**
 * Created by rayo on 10/20/16.
 */

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class AvaluoValidoJsonResult {

    @SerializedName("AvaluoValido")
    @Expose
    private Integer avaluoValido;
    @SerializedName("DesStn")
    @Expose
    private Double desStn;
    @SerializedName("ErrorStatus")
    @Expose
    private String errorStatus;
    @SerializedName("TipoModelo")
    @Expose
    private String tipoModelo;
    @SerializedName("ValorEstimado")
    @Expose
    private Double valorEstimado;

    /**
     *
     * @return
     * The avaluoValido
     */
    public Integer getAvaluoValido() {
        return avaluoValido;
    }

    /**
     *
     * @param avaluoValido
     * The AvaluoValido
     */
    public void setAvaluoValido(Integer avaluoValido) {
        this.avaluoValido = avaluoValido;
    }

    /**
     *
     * @return
     * The desStn
     */
    public Double getDesStn() {
        return desStn;
    }

    /**
     *
     * @param desStn
     * The DesStn
     */
    public void setDesStn(Double desStn) {
        this.desStn = desStn;
    }

    /**
     *
     * @return
     * The errorStatus
     */
    public String getErrorStatus() {
        return errorStatus;
    }

    /**
     *
     * @param errorStatus
     * The ErrorStatus
     */
    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    /**
     *
     * @return
     * The tipoModelo
     */
    public String getTipoModelo() {
        return tipoModelo;
    }

    /**
     *
     * @param tipoModelo
     * The TipoModelo
     */
    public void setTipoModelo(String tipoModelo) {
        this.tipoModelo = tipoModelo;
    }

    /**
     *
     * @return
     * The valorEstimado
     */
    public Double getValorEstimado() {
        return valorEstimado;
    }

    /**
     *
     * @param valorEstimado
     * The ValorEstimado
     */
    public void setValorEstimado(Double valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

}
