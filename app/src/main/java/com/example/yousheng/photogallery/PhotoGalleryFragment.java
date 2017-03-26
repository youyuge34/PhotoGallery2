package com.example.yousheng.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousheng on 17/3/24.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
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
        updateItems();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, null);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        StaggeredGridLayoutManager manger = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        GridLayoutManager manger=new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(manger);
        setupAdapter();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search_menu,menu);

        //响应用户的搜索框输入
        MenuItem menuItem=menu.findItem(R.id.menu_item_search);
        //用getactionview方法取出对象
        final SearchView searchView= (SearchView) menuItem.getActionView();
        //显示搜索框暗示
        searchView.setQueryHint("Type in page number to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //用户确认搜索后调用
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: "+query);
                //将搜索值使用preference存储
                QueryPreferences.setStoredQuery(getActivity(),query);
//                updateItems();
                //自动收起键盘
//                searchView.clearFocus();
                updateItems();
                return true;
            }

            @Override
            //只要文本框里的文字有变化，回调方法就会执行
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: "+newText);
                return false;
            }
        });

        //让搜索框默认显示已保存的查询信息
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=QueryPreferences.getSroredQuery(getActivity());
                searchView.setQuery(query,false);
            }
        });
    }

    //若是点了clear,则清除存储的搜索信息并刷新（实机测试不行，不知原因，界面不刷新）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(),null);
                updateItems();
                return true;

        }
        return false;
    }

    private void updateItems() {
        String query=QueryPreferences.getSroredQuery(getActivity());
        new FetchItemsTask(query).execute();
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
        private TextView mDateTextView;
        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mDateTextView= (TextView) itemView.findViewById(R.id.fragment_photo_gallery_date_view);
            mImageView= (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        //绑定模型的数据到视图上
        public void bindGalleryItem(GalleryItem item) {
            Log.d("test", "bindGalleryItem: "+item.getMdate());
            mDateTextView.setText(item.getMdate());
            Glide.with(getActivity()).load(item.getmUrl()).into(mImageView);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mList;

        public PhotoAdapter(List<GalleryItem> list) {
            mList = list;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //不用parent而使用null会导致cardview间距失效
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item,parent,false);
            return new PhotoHolder(view);
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
            private String mQuery;

        public FetchItemsTask(String mQuery) {
            this.mQuery = mQuery;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if(mQuery==null){
                return new FlickrFecthr().fetchRecentPhotos();
            }else {
                return new FlickrFecthr().searchPhotos(mQuery);
            }

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
