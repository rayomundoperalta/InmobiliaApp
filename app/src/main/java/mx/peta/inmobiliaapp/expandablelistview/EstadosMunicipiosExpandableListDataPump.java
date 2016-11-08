package mx.peta.inmobiliaapp.expandablelistview;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rayo on 11/2/16.
 */

public class EstadosMunicipiosExpandableListDataPump {

    public static HashMap<String, List<Municipio>> getData() {

        return CatalogoEstadoMunicipio.getInstance();
    }
}
