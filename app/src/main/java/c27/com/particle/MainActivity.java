package c27.com.particle;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import com.google.gson.Gson;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import c27.com.particle.query.Page;
import c27.com.particle.query.ResponseResult;
import c27.com.particle.recycleradapter.Fruit;
import c27.com.particle.recycleradapter.FruitAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    private FruitAdapter fruitAdapter;
    private ArrayList<Fruit> fresh=new ArrayList<>();//刷新集合;//请求到的数据
    private RecyclerView recyclerView;
    private LinearLayoutManager ll;
    private SmartRefreshLayout smartRefreshLayout;
    public static List<Fruit> list=new ArrayList<>();
    private Toolbar bar;
    //发送请求的页面，为全局常量，方便提交数据的的时候修改
    public static int count;
    //总页数,为全局常量方便提交数据的时候修改
    public static int total_page;
    //加载更多数据库操作对象
    //封装图片集合的list
    private List imageList;
    private List<String> imageUri=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //页面执行需要展示的动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        //调用视图
        setContentView(R.layout.activity_main);

        showBar();//添加ToolBar状态栏
        ActionBar bar=getSupportActionBar();
        if(bar!=null){
            bar.setDisplayShowTitleEnabled(false);
            bar.setDisplayHomeAsUpEnabled(true);//设置将该图标设计为可以点击的图标
            bar.setHomeAsUpIndicator(R.drawable.home_toolbar_30dp);//添加左边图标
        }
        showBar();
        recyclerView();
        init();

    }

    /**
     * 初始化适配
     */
    private void recyclerView(){

        recyclerView=(RecyclerView)findViewById(R.id.recyclerviewId);
        ll=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        fruitAdapter=new FruitAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
    }
    /**
     * 初始化方法
     */
    private void init(){
        smartRefreshLayout=(SmartRefreshLayout)findViewById(R.id.smart_refresh_id);
        smartRefreshLayout.setEnableAutoLoadMore(true);//启用下拉加载更多
        smartRefreshLayout.setRefreshHeader(new BezierCircleHeader(this));
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
//        //在刷新的时候禁止列表操作
//        smartRefreshLayout.setDisableContentWhenRefresh(true);
//        //在加载得到时候禁止列表操作
//        smartRefreshLayout.setDisableContentWhenLoading(true);
        //设置下拉刷新监听
        //启用越界回弹
        smartRefreshLayout.setEnableOverScrollBounce(true);
        //进入自动刷新
//        smartRefreshLayout.autoRefresh();
        smartRefreshLayout.setEnableAutoLoadMore(true);
        //刷新到没有数据时候的方法
        smartRefreshLayout.finishLoadMoreWithNoMoreData();
        //设置释放后的动画回弹时长
        smartRefreshLayout.setReboundDuration(500);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    refresh();


            }
        });
    }

    //去除重复元素并且保持顺序
    public void removeDuplicateWithOrder(List<Fruit> removelist) {
        Set<Fruit> set = new HashSet<>();
        List<Fruit> newList = new ArrayList<>();
        for (Iterator<Fruit> iter = removelist.iterator(); iter.hasNext();) {
            Object e = iter.next();
            if (set.add((Fruit)e))
                newList.add((Fruit)e);
        }
        removelist.clear();
        removelist.addAll(newList);
        System.out.println( "移除重复元素后list集合的长度" + removelist.size());
        for(Fruit f:removelist) {
            System.out.println(f);
        }
    }




    /**
     *启动一个线程发起请求，把数据添加到RecyclerView中
     */
    private void sendRequest(){

        new Thread(new Runnable() {
            //构建请求体
            RequestBody postBody=new FormBody.Builder().add("page",String.valueOf("1")).add("descs","writeTime").build();

            @Override
            public void run() {
                try{
                    //构件请求对象
                    OkHttpClient client=new OkHttpClient();
                    //请求体
                    Request request=new Request.Builder()//构件请求体
                            .url("http://139.199.4.125/dev/info/findList").post(postBody).build();
                    Response response=client.newCall(request).execute();//请求返回的数据对象
                    if(response.isSuccessful()){//如果请求成功
                        String responseData=response.body().string();//获得返回的数据

                        Log.e("返回的数据:",responseData);
                        parseJSON(responseData);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 解析即送数据
     * @param jsonData
     */
    private void  parseJSON(String jsonData) {
        Gson gson = new Gson();
        //取出JSON解析对象
        //取出JSON解析对象
        //取出JSON解析对象
        //转换为当前对象
        ResponseResult result=gson.fromJson(jsonData,ResponseResult.class);
        Page page=result.getData();
        //总页数
        total_page=(int)page.getPages();
        Log.e("当前总页数:",""+total_page);
        //当前请求的页数
        int current=(int)page.getCurrent();
        count=current+1;


        for (Object obj:page.getRecords()){
            Map map=(Map)obj;//强制转换为集合
            Log.e("当前页面current:",page.getCurrent()+"");
            System.out.print(map);
            Set<String> s=map.keySet();//把所有的key转换为集合
//            Log.e("key为:",s+"");
//            Log.e("用户id为:",map.get("id")+"");
//            Log.e("标题:",map.get("title").toString());
//            Log.e("内容:",map.get("content").toString());
//            Log.e("上传时间:",map.get("timeStr")+"");
//            Log.e("浏览次数:",map.get("visitCount")+"");
//            Log.e("图片列表:",""+map.get("imageList"));
            //装图片集合的list
            if(map.get("imageList")!=null&& map.get("imageList") instanceof  List){
                imageList=(List)map.get("imageList");//获得图片的集合
                for(Object object:imageList){
                    Map m=(Map)object;
                    imageUri.add(m.get("fileName").toString());
//                Log.e("图片名称:",m.get("fileName")+"");

                }
            }
//            Log.e("图片集合:",""+imageUri);
            fresh.add(new Fruit(map.get("id").toString(),map.get("title").toString(),map.get("content").toString(),imageUri,map.get("visitCount").toString(),map.get("timeStr").toString()));
        }
    }


    /*
    刷新数据的方法
     */
    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest();
                try {
                    //线程休眠1秒
                    Thread.sleep(1000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新的数据放在最前面
                        list.addAll(0,fresh);
                        Log.e("List集合的长度:",list.size()+"");
                        removeDuplicateWithOrder(list);
                        Log.e("移除重复元素list集合的长度:",list.size()+"");
//                        Log.e("fresh集合的长度:",fresh.size()+"");
                        fresh.clear();
//                        Log.e("fresh清空后集合的长度:",fresh.size()+"");
                        //通知列表数据发生改变
                        fruitAdapter.notifyDataSetChanged();
                        //刷新关闭
                        smartRefreshLayout.finishRefresh(1000);
                    }
                });

            }

        }).start();
    }



    private void showBar(){
         bar=(Toolbar)findViewById(R.id.toolbar_id);
        setSupportActionBar(bar);

    }

}
