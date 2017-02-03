package mx.peta.inmobiliaapp.PayPal;

/**
 * Created by rayo on 1/30/17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {

    @SerializedName("shipping")
    @Expose
    private String shipping;
    @SerializedName("subtotal")
    @Expose
    private String subtotal;
    @SerializedName("tax")
    @Expose
    private String tax;

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

}
