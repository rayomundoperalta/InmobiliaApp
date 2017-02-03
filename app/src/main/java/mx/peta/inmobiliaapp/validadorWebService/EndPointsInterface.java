package mx.peta.inmobiliaapp.validadorWebService;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rayo on 10/20/16.
 */

public interface EndPointsInterface {
    //@GET("validador.svc/avaluoValido")
    @GET("avaluo.php")
    Call<AvaluoValido>  getAvaluo(
            @Query("tipologia") Double tipologia,
            @Query("CP") Double CP,
            @Query("delegacion") Double delegacion,
            @Query("entidad") Double entidad,
            @Query("proximidadUrbana") Double proximidadUrbana,
            @Query("claseInmueble") Double claseInmueble,
            @Query("vidautil") Double vidaUtil,
            @Query("superTerreno") Double superTerreno,
            @Query("superConstruido") Double superConstruido,
            @Query("valConst") Double valConst,
            @Query("valConcluido") Double valConcluido,
            @Query("revisadoManualmente") int revisadoManualment,
            @Query("USER") String USER,
            @Query("PASSWORD") String PASSWORD,
            @Query("sensibilidad") Double sensibilidad
    );
}

