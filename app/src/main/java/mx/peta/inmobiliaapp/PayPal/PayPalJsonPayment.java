package mx.peta.inmobiliaapp.PayPal;

/**
 * Created by rayo on 1/30/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
http://www.jsonschema2pojo.org/
{
    "amount": "414.82",
    "currency_code": "USD",
    "details": {
                 "shipping": "7.21",
                 "subtotal": "402.94",
                 "tax": "4.67"
               },
    "short_description": "sample item",
    "intent": "sale",
    "item_list": {
                  "items": [
                            {
                             "quantity": "2",
                             "name": "sample item #1",
                             "price": "87.50",
                             "currency": "USD",
                             "sku": "sku-12345678"
                            },
                            {
                             "quantity": "1",
                             "name": "free sample item #2",
                             "price": "0.00",
                             "currency": "USD",
                             "sku": "sku-zero-price"
                            },
                            {
                             "quantity": "6",
                             "name": "sample item #3 with a longer name",
                             "price": "37.99",
                             "currency": "USD",
                             "sku": "sku-33333"
                            }
                           ]
                 }
}
*/
public class PayPalJsonPayment {

    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("details")
    @Expose
    private Details details;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("intent")
    @Expose
    private String intent;
    @SerializedName("item_list")
    @Expose
    private ItemList itemList;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public ItemList getItemList() {
        return itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }

}

