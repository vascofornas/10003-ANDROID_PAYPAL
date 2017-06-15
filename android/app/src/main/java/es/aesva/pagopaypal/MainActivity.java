package es.aesva.pagopaypal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class MainActivity extends Activity {

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final int REQUEST_CODE_PAYMENT = 1;

    private static final String CONFIG_CLIENT_ID = "AVKmw_XwdfrvM1R0oC9hA7RkvXMBMVXPUZSadaBYxq81cC-1_0KX6CjjL7rl3DnBKfc_J63xCUHs7ueP";


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

            /* configuracion minima de la empresa*/
            //nombre empresa
            .merchantName("Mi tienda")
            //politica de privacidad
            .merchantPrivacyPolicyUri
                    (Uri.parse(" https://www.example.com/privacy "))
            //uso legal de la empresa
            .merchantUserAgreementUri(
                    Uri.parse("https://www.mi_tienda.com/legal"));


    PayPalPayment thingToBuy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Como para emplear cualquier API o aplicación de un tercero, se emplean los Intent para crear el pago
        Intent intent1 = new Intent(this, PayPalService.class);
        intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent1);


        //aqui hay tres datos importantes el precio el tipo de moneda y el nombre del objeto que vendes
        thingToBuy = new PayPalPayment(new BigDecimal("20"), "EUR",
                "articulo", PayPalPayment.PAYMENT_INTENT_SALE);
        //abrimos la class para pagar en paypal
        Intent intent2 = new Intent(this,
                PaymentActivity.class);
        intent2.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent2, REQUEST_CODE_PAYMENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //si la operacion es OK
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data
                    .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    // informacion extra del pedido
                    System.out.println("info: " + confirm.toJSONObject().toString(4));
                    System.out.println("info2: " + confirm.getPayment().toJSONObject()
                            .toString(4));
                    System.out.println("Orden procesada");
                    Toast.makeText(getApplicationContext(), "Orden procesada",
                            Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //si la operacion no es OK
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Orden NO procesada",
                    Toast.LENGTH_LONG).show();

        }
    }

}
