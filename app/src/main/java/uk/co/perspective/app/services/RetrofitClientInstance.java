package uk.co.perspective.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.0.92:5000/"; /*//Test URL Only needs to be changed*/
    //private static final String BASE_URL = "http://api.mybirdy.co.uk:53123/";

    private static Context ThisContext;

    public static Retrofit getRetrofitInstance(Context context) {

        ThisContext = context;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ThisContext);

        String BASE_URL = Objects.requireNonNull(sharedPreferences.getString("endpoint_address", "http://api.myperspective.cloud:52123/"));

        if (!BASE_URL.contains("http://") && !BASE_URL.contains("https://"))
        {
            BASE_URL = "http://" + BASE_URL;
        }

        if (!BASE_URL.contains("/api/"))
        {
            BASE_URL = BASE_URL + "/api/";
        }

        if (ThisContext != null) {

            if (retrofit == null) {

                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {

                        Request originalRequest = chain.request();



                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + sharedPreferences.getString("token", "000000"))
                                .header("User-Agent", "perspective_Sales_Android")
                                .header("Connection", "close")
                                .method(originalRequest.method(), originalRequest.body())
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

                retrofit = new retrofit2.Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();
            }
        }

        return retrofit;
    }
}
