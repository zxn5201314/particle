package c27.com.particle.http;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpUtil {


    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                                                .readTimeout(10000,TimeUnit.MILLISECONDS)
                                                .writeTimeout(10000,TimeUnit.MILLISECONDS).build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");//以二进制的方式传输
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    //下载文件的方法
    public static void downloadFile(String url, final ProgressListener listener, Callback callback){

        OkHttpClient client = okHttpClient.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(),listener)).build();
            }
        }).build();
        //构建请求体
        Request request  = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送文件的方法
     * @param url//要传输图片的uri地址
     * @param listener//为进度上传监听
     * @param callback//java回调接口
     * @param files
     */
    public static void postFile(String url, final ProgressListener listener, Callback callback, File...files){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Log.i("周训年","files[i].getName()=="+files[0].getName());
        builder.addFormDataPart("filename",files[0].getName());
        builder.addFormDataPart("position","0");
        builder.addFormDataPart("file",files[0].getName(), RequestBody.create(MediaType.parse("application/octet-stream"),files[0]));

        MultipartBody multipartBody = builder.build();

        Request request  = new Request.Builder().url(url).post(new ProgressRequestBody(multipartBody,listener)).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static String doGet(String url){
        Request request  = new Request.Builder().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response!=null){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void doGet(String url,Callback callback){
        Request request  = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     *
     * @param url//请求地址
     * @param userName//用户名称
     * @param password//密码用户
     * @return
     */
    public static String doPost(String url,String userName,String password){

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("username",userName);
        formBodyBuilder.add("password",password);

        Request request  = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response!=null){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 带有回调请求的接口
     * @param url
     * @param userName
     * @param password
     * @param callback
     */
    public static void doPost(String url,String userName,String password,Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("username",userName);
        builder.addFormDataPart("password",password);


        //构建请求体，发起请求
        Request request  = new Request.Builder().url(url).post(builder.build()).build();
        okHttpClient.newCall(request).enqueue(callback);//请求成功回调
    }


    public static void crach(){
        CacheControl.Builder builder = new CacheControl.Builder();
        builder.noCache();//不使用缓存，全部走网络
        builder.noStore();//不使用缓存，也不存储缓存
        builder.onlyIfCached();//只使用缓存
        builder.noTransform();//禁止转码
        builder.maxAge(10, TimeUnit.MILLISECONDS);//指示客户机可以接收生存期不大于指定时间的响应。
        builder.maxStale(10, TimeUnit.SECONDS);//指示客户机可以接收超出超时期间的响应消息
        builder.minFresh(10, TimeUnit.SECONDS);//指示客户机可以接收响应时间小于当前时间加上指定时间的响应。
        CacheControl cache = builder.build();//cacheControl


        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.addFormDataPart("username","password");
        RequestBody requestBody = multipartBodyBuilder.build();
        Request request = new Request.Builder().post(requestBody).cacheControl(cache).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){

                }
            }
        });
    }

}
