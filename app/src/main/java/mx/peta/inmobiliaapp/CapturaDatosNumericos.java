package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CapturaDatosNumericos extends AppCompatActivity {

    EditText editTextCP;
    EditText editTextVidaUtil;
    EditText editTextSuperTerreno;
    EditText editTextSuperConstruida;
    EditText editTextValorConstruccion;
    EditText editTextValorConcluido;


    Button btnSiguiente;

    Propiedad propiedad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_datos_numericos);

        propiedad = Propiedad.getInstance();

        editTextCP = (EditText) findViewById(R.id.editTextCP);
        editTextVidaUtil = (EditText) findViewById(R.id.editTextVidaUtil);
        editTextSuperTerreno = (EditText) findViewById(R.id.editTextSuperTerreno);
        editTextSuperConstruida = (EditText) findViewById(R.id.editTextSuperConstruida);
        editTextValorConstruccion = (EditText) findViewById(R.id.editTextValorConstruccion);
        editTextValorConcluido = (EditText) findViewById(R.id.editTextValorConcluido);




            btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
            btnSiguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = editTextCP.getText().toString();
                    if (s.compareTo("") != 0) { propiedad.setCP((Double.valueOf(s)).doubleValue()); }
                    s = editTextVidaUtil.getText().toString();
                    if (s.compareTo("") != 0) { propiedad.setVidaUtil((Double.valueOf(s)).doubleValue()); }
                    s = editTextSuperTerreno.getText().toString();
                    if (s.compareTo("") != 0) { propiedad.setSuperTerreno((Double.valueOf(s)).doubleValue()); }
                    s = editTextSuperConstruida.getText().toString();
                    if (s.compareTo("") != 0) { propiedad.setSuperConstruido((Double.valueOf(s)).doubleValue()); }
                    s = editTextValorConstruccion.getText().toString();
                    if (s.compareTo("") != 0) { propiedad.setValConst((Double.valueOf(s)).doubleValue()); }
                    s = editTextValorConcluido.getText().toString();
                    if (s.compareTo("") != 0) { propiedad.setValConcluido((Double.valueOf(s)).doubleValue()); }

                    if (propiedad.getCP() > 0.0 ||
                            propiedad.getVidaUtil() > 0.0 ||
                            propiedad.getSuperTerreno() > 0.0 ||
                            propiedad.getSuperConstruido() > 0.0 ||
                            propiedad.getValConst() > 0.0 ||
                            propiedad.getValConcluido() > 0.0) {
                        Intent intent = new Intent(getApplicationContext(), CapturaCategorias.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),"Se deben introducir todos los valores, no se aceptan ceros",Toast.LENGTH_LONG).show();
                    }
                }
            });

    }
}
