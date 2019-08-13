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

public class RetrofitServiceGenerator {

    private static final String BASE_URL = "https://annetog.gotenna.com/";
    private static Retrofit singleInstance;

    private static Retrofit getRetrofit() {

        if (singleInstance == null) {
            singleInstance = new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
              .build();
        }
        return singleInstance;
    }

    public static PlacesService getPlacesService() {
        return getRetrofit().create(PlacesService.class);
    }
}
