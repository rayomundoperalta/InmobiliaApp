package mx.peta.inmobiliaapp.PayPal;

/**
 * Created by rayo on 1/30/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
http://www.jsonschema2pojo.org/
{"client": {
   "environment": "mock",
   "paypal_sdk_version": "2.15.1",
   "platform": "Android",
   "product_name": "PayPal-Android-SDK"
   },
 "response": {
   "create_time": "2014-07-18T18:46:55Z",
   "id": "PAY-18X32451H0459092JKO7KFUI",
   "intent": "sale",
   "state": "approved"
   },
 "response_type": "payment"
}
*/


public class PayPalJson {

    @SerializedName("client")
    @Expose
    private Client client;
    @SerializedName("response")
    @Expose
    private Response response;
    @SerializedName("response_type")
    @Expose
    private String responseType;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

}
