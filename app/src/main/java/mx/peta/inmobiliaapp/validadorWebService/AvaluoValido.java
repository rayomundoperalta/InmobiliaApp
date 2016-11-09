package mx.peta.inmobiliaapp.validadorWebService;

/**
 * Created by rayo on 10/20/16.
 */
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class AvaluoValido {
    @SerializedName("avaluoValidoJsonResult")
    @Expose
    private AvaluoValidoJsonResult avaluoValidoJsonResult;

    /**
     *
     * @return
     * The avaluoValidoJsonResult
     */
    public AvaluoValidoJsonResult getAvaluoValidoJsonResult() {
        return avaluoValidoJsonResult;
    }

    /**
     *
     * @param avaluoValidoJsonResult
     * The avaluoValidoJsonResult
     */
    public void setAvaluoValidoJsonResult(AvaluoValidoJsonResult avaluoValidoJsonResult) {
        this.avaluoValidoJsonResult = avaluoValidoJsonResult;
    }
}
