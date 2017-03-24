package com.example.yousheng.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by yousheng on 17/3/24.
 */
public class PhotoGalleryFragment extends Fragment{
private RecyclerView mRecyclerView;

    public static Fragment newInstance() {
        return new PhotoGalleryFragment();
    }

    //
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //开启新线程发送url，获取返回的数据
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,null);

        mRecyclerView= (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        StaggeredGridLayoutManager manger=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manger);

        return v;
    }

    //创建一个后台进程用来获取url返回的数据，防止主线程ANR，主线程不允许网络连接行为
    private class FetchItemsTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... params) {
            try{
                String result=new FlickrFecthr().getUrlString("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1");
                Log.d("test", "doInBackground: "+ result);
            } catch (IOException e) {
                Log.e("test", "doInBackground: failed "+e,e );
            }
            return null;
        }
    }
}
