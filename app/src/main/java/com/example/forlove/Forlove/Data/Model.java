package com.example.forlove.Forlove.Data;

import android.util.Log;

import com.example.forlove.Forlove.ViewModel.LoginViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class Model {
    private static volatile Model instance;
    private Retrofit retrofit;
    private Model(){init(); }
    public static Model getInstance() {
        if (instance == null) {
            synchronized (Model.class){
                if (instance==null)
                    instance = new Model();
            }
        }
        return instance;
    }
    /**
     * 提前连接上服务器
     */
    public void init(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
         retrofit= new Retrofit.Builder()
                .baseUrl("http://www.xiaowen520.xyz")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public interface ApiService {
        @POST("/add2/")
        Call<User> addUser(@Body RequestBody user);
        @GET("/check_account/{account}")
        Call<User> checkUser(@Path("account") String account);
    }
    public void getUser(String account, LoginViewModel.getOrsetUser api){
        ApiService request = retrofit.create(ApiService.class);
        Call<User> call = request.checkUser(account);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("查找结果"+ response.body().getResult());
                Log.i("thread","get User On Response:Current Thread: "+Thread.currentThread());
                api.getuser(response.body());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("请求失败"+call.request());
                System.out.println(t.getMessage());
                api.NoInternet();
            }
        });
    }
    public void setUser(String account,String password,LoginViewModel.getOrsetUser api){
        ApiService request = retrofit.create(ApiService.class);
        Map<String,String> user = new HashMap<>();
        user.put("account",account);
        user.put("password",password);
        JSONObject jsonObj = new JSONObject(user);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObj));
        System.out.println(String.valueOf(jsonObj));
        Call<User> call = request.addUser(body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                api.setuser(response.body().getResult());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("请求失败"+call.request());
                System.out.println(t.getMessage());
                api.NoInternet();
            }
        });
    }
}
