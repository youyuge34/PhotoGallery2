package com.example.yousheng.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by yousheng on 17/3/26.
 */

//用来管理保存的查询字符串
public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    public static String getSroredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY,null);
    }

    public static void setStoredQuery(Context context,String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY,query)
                //apply方法首先在内存中变更，然后在后台线程上真正把数据写入文件
                .apply();
    }
}
