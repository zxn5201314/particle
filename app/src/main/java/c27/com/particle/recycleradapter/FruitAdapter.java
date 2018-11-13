package c27.com.particle.recycleradapter;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import c27.com.particle.R;
import c27.com.particle.query.Page;
import c27.com.particle.query.ResponseResult;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static c27.com.particle.server.ServerInfo.DOWN_PIC;
import static c27.com.particle.server.ServerInfo.REQUEST_ADDR;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private Context mContext;
    private List<Fruit> mFruitList;
    private List<String> imageList=new ArrayList<>();
    static class ViewHolder extends RecyclerView.ViewHolder{
        View fruitView;
        CardView cardView;
        ImageView imageView,imageView1,imageView2;
        TextView textView;
        TextView textContent;
        TextView visitCount;
        TextView time;
        /**
         * 此处进行图片文字视图的添加
         * @param view
         */
        public ViewHolder(View view){
            super(view);
            fruitView=view;
            cardView=(CardView)view;
            imageView=(AppCompatImageView)view.findViewById(R.id.image_fruit_id);//第一张图片
            imageView1=(AppCompatImageView)view.findViewById(R.id.image_fruit_id1);//第一张图片
            imageView2=(AppCompatImageView)view.findViewById(R.id.image_fruit_id2);//第一张图片
            textView=(TextView)view.findViewById(R.id.fruit_name_id);
            textContent=(TextView)view.findViewById(R.id.fruit_contetn_id);
            visitCount=(TextView)view.findViewById(R.id.visit_tv1);
            time=(TextView)view.findViewById(R.id.time_tv2);
            //设置两行之后显示省路号


        }
    }
    //高中方法
    public FruitAdapter(List<Fruit> fruitList){
        mFruitList=fruitList;
        Log.e("mfruit集合的长度:",mFruitList.size()+"");
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if(mContext==null){
            mContext=parent.getContext();
        }
        //添加集合
        View view= LayoutInflater.from(mContext).inflate(R.layout.fruit_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);//找到该视图
        return holder;

    }
    //将在此方法里面进行数据处理,从网络获取数据
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Fruit fruit=mFruitList.get(position);
        imageList=fruit.getImageList();//获得集合
        holder.textView.setText(fruit.getName());//设置标题
        holder.textContent.setText("\t\t\t\t"+fruit.getContent());//设置内容
        holder.visitCount.setText(fruit.getVisitCount().substring(0,fruit.getVisitCount().indexOf(".")));
        holder.time.setText(fruit.getTime());//设置发布时间
        loadListToImage(imageList,holder);

    }

    /**
     * 判断集合内容加载到桌面
     *
     */
    private void loadListToImage(List<String > list,ViewHolder viewHolder){
        if(list.size()>0&&list.size()>3){
            //判断视图是否隐藏
            if(viewHolder.imageView.getVisibility()==View.GONE||viewHolder.imageView1.getVisibility()==View.GONE||viewHolder.imageView2.getVisibility()==View.GONE){
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.imageView1.setVisibility(View.VISIBLE);
                viewHolder.imageView2.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext).load(DOWN_PIC+list.get(0)).animate(R.anim.glide_anim).crossFade().into(viewHolder.imageView);
            Glide.with(mContext).load(DOWN_PIC+list.get(1)).animate(R.anim.glide_anim).crossFade().into(viewHolder.imageView1);
            Glide.with(mContext).load(DOWN_PIC+list.get(2)).animate(R.anim.glide_anim).crossFade().into(viewHolder.imageView2);
        }else if(list.size()>0&&list.size()==1){
            //判断视图是否隐藏
            if(viewHolder.imageView.getVisibility()==View.GONE||viewHolder.imageView1.getVisibility()==View.GONE||viewHolder.imageView2.getVisibility()==View.GONE){
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.imageView1.setVisibility(View.VISIBLE);
                viewHolder.imageView2.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext).load(DOWN_PIC+list.get(0)).animate(R.anim.glide_anim).crossFade().into(viewHolder.imageView);
        }else if (list.size()>0&&list.size()==2){
            //判断视图是否隐藏
            if(viewHolder.imageView.getVisibility()==View.GONE||viewHolder.imageView1.getVisibility()==View.GONE||viewHolder.imageView2.getVisibility()==View.GONE){
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.imageView1.setVisibility(View.VISIBLE);
                viewHolder.imageView2.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext).load(DOWN_PIC+list.get(0)).animate(R.anim.glide_anim).crossFade().into(viewHolder.imageView);
            Glide.with(mContext).load(DOWN_PIC+list.get(1)).animate(R.anim.glide_anim).crossFade().into(viewHolder.imageView1);
        }else if (list.size()==0){//如果集合等于0，三个视图隐藏
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.imageView1.setVisibility(View.GONE);
            viewHolder.imageView2.setVisibility(View.GONE);
        }


    }
    @Override
    public int getItemCount(){
        return mFruitList.size();
    }


    //下面两个方法提供给页面刷新和加载时调用
    public void addData(List<Fruit> addList) {
        //增加数据
        int position = mFruitList.size();
        mFruitList.addAll(position, addList);
        notifyItemInserted(position);
    }

    public void refresh(List<Fruit> newList) {
        //刷新数据
        mFruitList.removeAll(newList);
        mFruitList.addAll(newList);
        notifyDataSetChanged();
    }
/**
 * 构建请求体获取浏览量
 */
private void sendRequest(){

    new Thread(new Runnable() {
        //构建请求体
        RequestBody postBody=new FormBody.Builder().add("id",String.valueOf("visit")).build();
        @Override
        public void run() {
            try{
                //构件请求对象
                OkHttpClient client=new OkHttpClient();
                //请求体
                Request request=new Request.Builder()//构件请求体
                        .url(REQUEST_ADDR).post(postBody).build();
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
        //转换为当前对象
        ResponseResult result=gson.fromJson(jsonData,ResponseResult.class);
        Page page=result.getData();

        for (Object obj:page.getRecords()){
            Map map=(Map)obj;//强制转换为集合

            Log.e("浏览次数:",map.get("visitCount")+"");

        }
    }
}

