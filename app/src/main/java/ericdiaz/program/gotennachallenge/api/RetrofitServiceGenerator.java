package ericdiaz.program.gotennachallenge.api;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A retrofit implementation class to connect to network
 * <p>
 * Created 8/13/19
 *
 * @author EricDiaz
 */

public final class RetrofitServiceGenerator {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private static final String BASE_URL = "https://annetog.gotenna.com/";
    private static Retrofit singleRetrofitInstance;
    private static PlacesService singlePlacesService;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    private RetrofitServiceGenerator() {}

    //==============================================================================================
    // Class Static Methods
    //==============================================================================================

    private static Retrofit getRetrofit() {
        if (singleRetrofitInstance == null) {

            singleRetrofitInstance = new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
              .build();
        }

        return singleRetrofitInstance;
    }

    public static PlacesService getPlacesService() {
        if (singlePlacesService == null) {

            singlePlacesService = getRetrofit().create(PlacesService.class);
        }

        return singlePlacesService;
    }
}
