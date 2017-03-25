package com.example.yousheng.photogallery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yousheng on 17/3/24.
 */

//网络连接专用类
public class FlickrFecthr {
    private static final String TAG = "FlickrFecthr";

    //从指定url获取原始数据并返回一个字节流数组
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        //创建URL对象
        URL url = new URL(urlSpec);
        //调动open方法创建一个指向要访问URL的连接对象
        //openConncetion方法返回一个URLconnection对象，而我们要访问的是http地址，所以强制转型
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(8000);
        connection.setRequestMethod("GET");
        connection.setReadTimeout(8000);


        try {
            //字节输出流，用于把读取到的网络数据写入此out字节流数组中
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //调用了这个方法后，才会真正连接到指定的url地址
            InputStream in = connection.getInputStream();

            //一般都会使用getResponseCode()来获取服务器返回的HTTP状态码，以便判断请求是否成功(404、502等就是失败了）
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + " :with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            //不停地按照buffer.length的长度把in字节流读出保存到buffer中，返回实际读到的byte数，若读完了返回-1
            while ((bytesRead = in.read(buffer)) > 0) {
                //从0开始，length为bytesread，将buffer中的字节写入到out流中
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    //将url请求，服务器回传的数据转换成string返回
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    //构建请求url,返回item模型的list
    public List<GalleryItem> fetchItems(){
        List<GalleryItem> list=new ArrayList<>();
        try {
            String url="http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";
            String jsonString=getUrlString(url);
            //使用构造函数，把json数据解析成Java对象，生成对象树
            JSONObject jsonBody=new JSONObject(jsonString);
            //将json树中的对象解析成模型层对象并存入list
            parseItem(list,jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //将json对象解析成模型层GalleryItem的对象
    private void parseItem(List<GalleryItem> items,JSONObject jsonBody) throws IOException,JSONException{
        //获得json数组
        JSONArray photoJsonArray=jsonBody.getJSONArray("results");

        //遍历json数组，每一个json对象转为galleryItem模型对象并存入
        for(int i=0;i<photoJsonArray.length();i++){
            JSONObject jsonObject=photoJsonArray.getJSONObject(i);

            GalleryItem item=new GalleryItem();
            item.setmId(jsonObject.getString("_id"));
            item.setMdate(jsonObject.getString("desc"));
            item.setmUrl(jsonObject.getString("url"));

            items.add(item);
        }
    }
}
