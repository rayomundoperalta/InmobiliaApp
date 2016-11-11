package mx.peta.inmobiliaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mx.peta.inmobiliaapp.expandablelistview.CustomExpandableListAdapter;
import mx.peta.inmobiliaapp.expandablelistview.EstadosMunicipiosExpandableListDataPump;
import mx.peta.inmobiliaapp.expandablelistview.Municipio;

public class CapturaEstadoMunicipio extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<Municipio>> expandableListDetail;
    private Propiedad propiedad = Propiedad.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_estado_municipio);

        // Inicializamos registro propiedad
        propiedad.setTakingPhotoState(false);

        // localizamos el elemento grafico que implementa el expandable list view
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        // le ponemos un header al expandable list view
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.expandable_list_header,expandableListView,false);
        expandableListView.addHeaderView(headerView);

        // nos traemos la estructura de datos del catalogo
        expandableListDetail = EstadosMunicipiosExpandableListDataPump.getData();

        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        Collections.sort(expandableListTitle);
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String claveMunicipio = (expandableListDetail.get(expandableListTitle.get(groupPosition))
                        .get(childPosition)).getClaveMunicipio();

                System.out.println("Inmobilia " + groupPosition + ":" + childPosition + " " +
                        expandableListTitle.get(groupPosition)
                        + " -> "
                        + (expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition)).getNombreMunicipio() + " " +
                        claveMunicipio
                );

                Propiedad propiedad = Propiedad.getInstance();
                String s1 = null, s2 = null;
                switch(claveMunicipio.length()) {
                    case 4:
                        s1 = claveMunicipio.substring(0, 1);
                        s2 = claveMunicipio.substring(1, 4);
                        break;
                    case 5:
                        s1 = claveMunicipio.substring(0, 2);
                        s2 = claveMunicipio.substring(2, 5);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"Esto no debe pasar jamas", Toast.LENGTH_LONG).show();
                }

                Double e = (Double.valueOf(s1)).doubleValue();
                Double m = (Double.valueOf(s2)).doubleValue();
                propiedad.setEntidad(e);
                propiedad.setDelegacion(m);
                Intent intent = new Intent(getApplicationContext(), CapturaDatosNumericos.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }
}
