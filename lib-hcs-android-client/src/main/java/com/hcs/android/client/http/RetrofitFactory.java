package com.hcs.android.client.http;

import android.util.Log;

import com.hcs.android.client.entity.RetrofitConfig;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private RetrofitFactory(){

    }
    public static Retrofit getRetrofitInstance(RetrofitConfig retrofitConfig, List<Interceptor> interceptorList){
        if(retrofitConfig.isAddLogInterceptor()){
            interceptorList.add(getLogInterceptor(retrofitConfig));
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(retrofitConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(retrofitConfig.isRetryOnConnectionFailure())//错误重新连接
                .readTimeout(retrofitConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(retrofitConfig.getWriteTimeout(), TimeUnit.SECONDS);
        if(interceptorList != null && interceptorList.size() > 0){
            for(Interceptor interceptor: interceptorList){
                builder.addInterceptor(interceptor);
            }
        }
        OkHttpClient httpClient =  builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(retrofitConfig.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    private static Interceptor getLogInterceptor(RetrofitConfig retrofitConfig){

        //添加http日志
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("logInterceptor", message);
            }
        });
        logInterceptor.setLevel(retrofitConfig.getLogLevel()); //设置拦截器,不要忘记设置日志的级别,否则会不回调数据

//        Interceptor requestInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                // 以拦截到的请求为基础创建一个新的请求对象，然后插入Header
//                Request.Builder builder = chain.request().newBuilder();
//                builder.addHeader("Content-Type", retrofitConfig.getContentType())
//                        .addHeader("charset", retrofitConfig.getCharset());
//                Request request = builder.build();
//                return chain.proceed(request);
//            }
//        };
//        interceptorList.add(requestInterceptor);
        return logInterceptor;
    }
}
