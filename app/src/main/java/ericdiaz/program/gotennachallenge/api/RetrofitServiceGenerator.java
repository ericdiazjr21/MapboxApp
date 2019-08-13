package ericdiaz.program.gotennachallenge.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceGenerator {

    private static final String BASE_URL = "https://annetog.gotenna.com";
    private static Retrofit singleInstance;

    private static Retrofit getRetrofit() {

        if (singleInstance == null) {
            singleInstance = new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
        }
        return singleInstance;
    }

    public PlacesService getPlacesService() {
        return getRetrofit().create(PlacesService.class);
    }
}
