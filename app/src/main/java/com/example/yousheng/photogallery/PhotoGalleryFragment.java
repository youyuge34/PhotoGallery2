package com.example.yousheng.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousheng on 17/3/24.
 */
public class PhotoGalleryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<GalleryItem> mList = new ArrayList<>();

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
        View v = inflater.inflate(R.layout.fragment_photo_gallery, null);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        StaggeredGridLayoutManager manger = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manger);
        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        /** isAdded():
         *  Return true if the fragment is currently added to its activity.
         */
        if(isAdded()){
            mRecyclerView.setAdapter(new PhotoAdapter(mList));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        //绑定模型的数据到视图上
        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mList;

        public PhotoAdapter(List<GalleryItem> list) {
            mList = list;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mList.get(position);
            holder.bindGalleryItem(item);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    //创建一个后台进程用来获取url返回的数据，防止主线程ANR，主线程不允许网络连接行为
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
            //save

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            return new FlickrFecthr().fetchItems();
        }

        //此方法在doInBackground方法完成后执行，且在主线程中执行，可以更新UI，参数为后台线程返回的参数
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            //传递后台线程的模型数据到前台，并更新UI
            mList=items;
            setupAdapter();
        }
    }
}
