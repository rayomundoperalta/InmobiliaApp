package mx.peta.inmobiliaapp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;

public class InmobiliaPayPalPayment extends Activity implements OnClickListener {
    // Local references to our amounts and description
    private double _theSubtotal;
    private double _taxAmount;
    private String _productDescription;
    private String _price;

    /*
     * PayPal library related fields
     */
    private CheckoutButton launchPayPalButton;
    final static public int PAYPAL_BUTTON_ID = 10001;
    private static final int REQUEST_PAYPAL_CHECKOUT = 2;
    // Keeps a reference to the progress dialog
    private ProgressDialog _progressDialog;
    private boolean _paypalLibraryInit = false;
    private boolean _progressDialogRunning = false;

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("InmobiliaApp PayPalPayment on resume");
        if (PayPal.getInstance() == null)
            System.out.println("InmobiliaApp PayPalPayment no esta inicializado");
        loadReviewPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("InmobiliaApp PayPalPayment onCreate");
        super.onCreate(savedInstanceState);
        // Initialize the library.
        Thread libraryInitializationThread = new Thread() {
            public void run() {
                System.out.println("InmobiliaApp PayPalPayment corremos la inicialización");
                initLibrary();
            }
        };
        libraryInitializationThread.start();
    }

    public void loadReviewPage() {
        System.out.println("InmobiliaApp PayPalPayment loadReviewPage");
        setContentView(R.layout.activity_pay_pal_payment);

        // Do all our setup of the Review page
        // Set up all the Pizza-related strings
        _productDescription = "Paquete de estimaciones";
        ((TextView) findViewById(R.id.DescripciónProducto)).setText(_productDescription);
        //DecimalFormat formateador = new DecimalFormat("###,###");
        //formateador.setMaximumFractionDigits(2);
        double priceAmount = 50.0 * 1.05;
        _price = String.format("%.2f", priceAmount);

        String buffer = String.format("%.2f", priceAmount);
        ((TextView) findViewById(R.id.Precio)).setText("$" + buffer);

        _theSubtotal = priceAmount;// saving this here so we can access it later
        // to account for tax
        _taxAmount = priceAmount * .16;

        priceAmount += _taxAmount;

        buffer = String.format("%.2f", _taxAmount);
        ((TextView) findViewById(R.id.importeIVA)).setText("$" + buffer);

        buffer = String.format("%.2f", priceAmount);
        ((TextView) findViewById(R.id.importeTotal)).setText("$" + buffer);
        // insert the PayPal button
        // Check if the PayPal Library has been initialized yet. If it has, show
        // the "Pay with PayPal button"
        // If not, show a progress indicator and start a loop that keeps
        // checking the init status
        if (_paypalLibraryInit) {
            System.out.println("InmobiliaApp PayPalPayment Encontre inicializada la libreria");
            showPayPalButton();
        } else {
            System.out.println("InmobiliaApp PayPalPayment No está inicializada la libreria <-----------------");
            // Display a progress dialog to the user and start checking for when
            // the initialization is completed
            _progressDialog = new ProgressDialog(this);
            _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progressDialog.setMessage("Loading PayPal Payment Library");
            _progressDialog.setCancelable(false);
            _progressDialog.show();
            _progressDialogRunning = true;
            Thread newThread = new Thread(checkforPayPalInitRunnable);
            newThread.start();
        }

    }

    // PayPal Activity Results. This handles all the responses from the PayPal
    // Payments Library
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println("InmobiliaApp PayPalPayment onActivityResult");
        if (requestCode == REQUEST_PAYPAL_CHECKOUT) {
            System.out.println("InmobiliaApp PayPalPayment Payment result");
            PayPalActivityResult(requestCode, resultCode, intent);
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == (CheckoutButton) findViewById(PAYPAL_BUTTON_ID)) {
            System.out.println("InmobiliaApp PayPalPayment se va a hacer el pago - onClick");
            PayPalButtonClick(arg0);
        }
    }

    public void paymentSucceeded(String payKey) {
        // We could show the transactionID to the user
        System.out.println("InmobiliaApp PayPalPayment   S u c c e s s !");
        System.out.println("InmobiliaApp PayPalPayment Payment Key: " + payKey);
        Intent intent = new Intent();
        intent.putExtra(PaypalConstants.PAYPAL_RESULT, PaypalConstants.PAYPAL_SUCCEED);
        intent.putExtra(PaypalConstants.PAY_KEY, payKey);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void paymentFailed(String errorID, String errorMessage) {
        // We could let the user know the payment failed here
        System.out.println("InmobiliaApp PayPalPayment payment Failure");
        System.out.println("InmobiliaApp PayPalPayment Error: " + errorMessage);
        System.out.println("InmobiliaApp PayPalPayment Error ID: "	+ errorID);
        Intent intent = new Intent();
        intent.putExtra(PaypalConstants.PAYPAL_RESULT, PaypalConstants.PAYPAL_ERROR);
        intent.putExtra(PaypalConstants.PAYPAL_ERROR_MSG, errorMessage);
        intent.putExtra(PaypalConstants.PAYPAL_ERROR_ID, errorID);
        setResult(RESULT_OK, intent);
    }

    public void paymentCanceled() {
        // We could tell the user that the payment was canceled
        System.out.println("InmobiliaApp PayPalPayment payment Canceled.");
        Intent intent = new Intent();
        intent.putExtra(PaypalConstants.PAYPAL_RESULT, PaypalConstants.PAYPAL_CANCELED);
        setResult(RESULT_OK, intent);
    }

    /**********************************
     * PayPal library related methods
     **********************************/

    // This lets us show the PayPal Button after the library has been
    // initialized
    final Runnable showPayPalButtonRunnable = new Runnable() {
        public void run() {
            showPayPalButton();
        }
    };

    // This lets us run a loop to check the status of the PayPal Library init
    final Runnable checkforPayPalInitRunnable = new Runnable() {
        public void run() {
            checkForPayPalLibraryInit();
        }
    };

    // This method is called if the Review page is being loaded but the PayPal
    // Library is not
    // initialized yet.
    private void checkForPayPalLibraryInit() {
        // Loop as long as the library is not initialized
        while (_paypalLibraryInit == false) {
            System.out.println("InmobiliaApp PayPalPayment estoy verificando que pay pal este inicializado");
            try {
                // wait 1/2 a second then check again
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Show an error to the user
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.EstiloAlertas);
                builder.setMessage("Error initializing PayPal Library")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Could do anything here to handle the
                                        // error
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        // If we got here, it means the library is initialized.
        // So, add the "Pay with PayPal" button to the screen
        runOnUiThread(showPayPalButtonRunnable);
    }

    /**
     * The initLibrary function takes care of all the basic Library
     * initialization.
     *
     * @return The return will be true if the initialization was successful and
     *         false if
     */
    public void initLibrary() {
        System.out.println("InmobiliaApp PayPalPayment initLibrary");
        PayPal pp = PayPal.getInstance();
        // If the library is already initialized, then we don't need to
        // initialize it again.
        if (pp == null) {
            // This is the main initialization call that takes in your Context,
            // the Application ID, and the server you would like to connect to.
            pp = PayPal.initWithAppID(this, "APP-80W284485P519543T",
                    PayPal.ENV_SANDBOX);

            // -- These are required settings.
            pp.setLanguage("en_US"); // Sets the language for the library.
            // --

            // -- These are a few of the optional settings.
            // Sets the fees payer. If there are fees for the transaction, this
            // person will pay for them. Possible values are FEEPAYER_SENDER,
            // FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and
            // FEEPAYER_SECONDARYONLY.
            pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
            // Set to true if the transaction will require shipping.
            pp.setShippingEnabled(true);
            // Dynamic Amount Calculation allows you to set tax and shipping
            // amounts based on the user's shipping address. Shipping must be
            // enabled for Dynamic Amount Calculation. This also requires you to
            // create a class that implements PaymentAdjuster and Serializable.
            pp.setDynamicAmountCalculationEnabled(false);
            // --
            _paypalLibraryInit = true;
            System.out.println("InmobiliaApp PayPalPayment Libreria inicializada");
        } else {
            System.out.println("InmobiliaApp PayPalPayment La libreria ya estaba inicializada");
            _paypalLibraryInit = true;
        }
    }

    /** this method generates the PayPal checkout button and adds it to the current view
     *  using the relative layout params
     */
    private void showPayPalButton() {
        removePayPalButton();
        // Back in the UI thread -- show the "Pay with PayPal" button
        // Generate the PayPal Checkout button and save it for later use
        PayPal pp = PayPal.getInstance();
        // launchPayPalButton = pp.getCheckoutButton(this, PayPal.BUTTON_278x43,
        //		CheckoutButton.TEXT_PAY);
        launchPayPalButton = pp.getCheckoutButton(this, PayPal.BUTTON_194x37,
                CheckoutButton.TEXT_PAY);

        // You'll need to have an OnClickListener for the CheckoutButton.
        launchPayPalButton.setOnClickListener(this);
        // add it to the layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 10;

        launchPayPalButton.setLayoutParams(params);
        launchPayPalButton.setId(PAYPAL_BUTTON_ID);
        ((RelativeLayout) findViewById(R.id.RelativeLayout01))
                .addView(launchPayPalButton);
        ((RelativeLayout) findViewById(R.id.RelativeLayout01))
                .setGravity(Gravity.CENTER_HORIZONTAL);
        ((RelativeLayout) findViewById(R.id.RelativeLayout01))
                .setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        if (_progressDialogRunning) {
            _progressDialog.dismiss();
            _progressDialogRunning = false;
        }
    }

    /* this method removes the PayPal button from the view
     */
    private void removePayPalButton() {
        // Avoid an exception for setting a parent more than once
        if (launchPayPalButton != null) {
            ((RelativeLayout) findViewById(R.id.RelativeLayout01))
                    .removeView(launchPayPalButton);
        }
    }

    /* method to handle PayPal checkout button onClick event
     * - this must be called from the onClick() method implemented by the application
     */
    public void PayPalButtonClick(View arg0) {
        System.out.println("InmobiliaApp PayPalPayment detectamos el click del PayPalButton");
        // Create a basic InmobiliaPayPalPayment.
        PayPalPayment payment = new PayPalPayment();
        // Sets the currency type for this payment.
        payment.setCurrencyType("MXN");
        // Sets the recipient for the payment. This can also be a phone
        // number.
        // payment.setRecipient("movile@peta.mx");
        payment.setRecipient("raymundo_peralta-facilitator@yahoo.com");
        // Sets the amount of the payment, not including tax and shipping
        // amounts.
        BigDecimal st = new BigDecimal(_theSubtotal);
        // Se redondea el numero, en BigDecimal Scale es el numero de cifras decimales
        st = st.setScale(2, RoundingMode.HALF_UP);
        payment.setSubtotal(st);
        // Sets the payment type. This can be PAYMENT_TYPE_GOODS,
        // PAYMENT_TYPE_SERVICE, PAYMENT_TYPE_PERSONAL, or
        // PAYMENT_TYPE_NONE.
        payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);


        // PayPalInvoiceData can contain tax and shipping amounts. It also
        // contains an ArrayList of PayPalInvoiceItem which can
        // be filled out. These are not required for any transaction.
        PayPalInvoiceData invoice = new PayPalInvoiceData();
        // Sets the tax amount.
        BigDecimal tax = new BigDecimal(_taxAmount);
        tax = tax.setScale(2, RoundingMode.HALF_UP);
        invoice.setTax(tax);

        // PayPalInvoiceItem has several parameters available to it. None of these parameters is required.
        PayPalInvoiceItem item1 = new PayPalInvoiceItem();
        // Sets the name of the item.
        item1.setName("Paquete de estimaciones");
        // Sets the ID. This is any ID that you would like to have associated with the item.
        item1.setID("1234");
        // Sets the total price which should be (quantity * unit price). The total prices of all PayPalInvoiceItem should add up
        // to less than or equal the subtotal of the payment.
        item1.setTotalPrice(new BigDecimal(_price));
        // Sets the unit price.
        item1.setUnitPrice(new BigDecimal(_price));
        // Sets the quantity.
        item1.setQuantity(1);
        // Add the PayPalInvoiceItem to the PayPalInvoiceData. Alternatively, you can create an ArrayList<PayPalInvoiceItem>
        // and pass it to the PayPalInvoiceData function setInvoiceItems().
        invoice.getInvoiceItems().add(item1);
        // Sets the InmobiliaPayPalPayment invoice data.
        payment.setInvoiceData(invoice);
        // Sets the merchant name. This is the name of your Application or
        // Company.
        payment.setMerchantName("InmobiliaApp");
        // Sets the description of the payment.
        payment.setDescription("Paquete de 5 estimaciones de valor");

        // Use checkout to create our Intent.
        Intent checkoutIntent = PayPal.getInstance()
                .checkout(payment, this /*, new ResultDelegate()*/);
        // Use the android's startActivityForResult() and pass in our
        // Intent.
        // This will start the library.
        startActivityForResult(checkoutIntent, REQUEST_PAYPAL_CHECKOUT);
        System.out.println("InmobiliaApp PayPalPayment se llamó a la rutina de PayPal que hace el pago");
    }

    /* This method handles the PayPal Activity Results. This handles all the responses from the PayPal
     * Payments Library.
     *  This method must be called from the application's onActivityResult() handler
     */
    public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                // The payment succeeded
                String payKey = intent
                        .getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
                this.paymentSucceeded(payKey);
                break;
            case Activity.RESULT_CANCELED:
                // The payment was canceled
                this.paymentCanceled();
                break;
            case PayPalActivity.RESULT_FAILURE:
                // The payment failed -- we get the error from the
                // EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
                String errorID = intent
                        .getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
                String errorMessage = intent
                        .getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
                this.paymentFailed(errorID, errorMessage);
        }
    }
}