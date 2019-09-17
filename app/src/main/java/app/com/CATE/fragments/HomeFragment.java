package app.com.CATE.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.TwitchActivity;
import app.com.CATE.adapters.HorizontalCategoryAdapter;
import app.com.CATE.adapters.VideoPostAdapter;
import app.com.CATE.DetailsActivity;
import app.com.CATE.MainActivity;
import app.com.CATE.interfaces.OnArrayClickListner;
import app.com.CATE.models.CategoryModel;
import app.com.youtubeapiv3.R;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.models.YoutubeDataModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HomeFragment extends Fragment {

    public String GOOGLE_YOUTUBE_API_KEY = "AIzaSyDDNXQW5vUsBy91h_swoSAc_uFFAG14Clo";//here you should use your api key for testing purpose you can use this api also
    public String PLAYLIST_ID = "PLHRoF1XPhCHXQhWkViQveuVa-k6P8_aD2";//here you should use your playlist id for testing purpose you can use this api also
    public String PLAYLIST_GET_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + PLAYLIST_ID + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();
    private ArrayList<YoutubeDataModel> nListData = new ArrayList<>();
    int requestNum = 0;
    String userID;
    String category_selected;
    boolean collision=true;
    //가로 카테고리
    private RecyclerView listview2;
    private HorizontalCategoryAdapter adapter2;

    private View preView;

    //정렬 기준
    String sortby;

    public MainActivity mainActivity;
    private ProgressBar progressBar,progressBarstart;

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        listview2.setLayoutManager(layoutManager);
    }

    public HomeFragment() {
// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();
        PLAYLIST_GET_URL = mainActivity.PLAYLIST_GET_URL;
        mList_videos = (RecyclerView) view.findViewById(R.id.mList_videos);
        mListData = mainActivity.listData;
        listview2 = (RecyclerView) view.findViewById(R.id.mList_horizontal_category);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarhome);
        progressBarstart = (ProgressBar) view.findViewById(R.id.progressbarfirst);
        progressBar.setVisibility(View.GONE);
        progressBarstart.setVisibility(View.GONE);

        initList(nListData);


        userID = mainActivity.strName;

        //스피너 생성 및 클릭시 동작 지정
        Spinner spinner = (Spinner)view.findViewById(R.id.spinnerSort);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortby = ""+parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortby = "인기순";
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getCategory(mainActivity.strName).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                try {

                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("전체");
                    for(int i =0; i < response.body().size(); i++) {
                        arrayList.add(response.body().get(i).getAsJsonObject().get("category_name").getAsString());
                    }

                    init(arrayList,0);

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

        mList_videos.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, final int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount();
                Log.e("sdsd","ssss : "+lastVisibleItemPosition);
                if (lastVisibleItemPosition == itemTotalCount-1) {
                    do{
                        progressBar.setVisibility(View.VISIBLE);
                        collision=false;
                        category_scroll_plus(lastVisibleItemPosition + 1);
                    }while(collision);

                    //리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                }

            }
        });

        return view;
    }

    private void category_scroll_plus(final int start){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getCategoryVideo(category_selected, sortby).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    for(int i=start; i < start+3; i++) {
                        JsonObject object = response.body().get(i).getAsJsonObject();

                        YoutubeDataModel youtubeObject = new YoutubeDataModel();
                        String thumbnail = "";
                        String video_id = "";
                        String cateName, video_kind, cateDetail;
                        int video_index, likes, dislikes;

                        cateName = object.get("title").getAsString();
                        video_kind = object.get("kind").getAsString();
                        cateDetail = object.get("url").getAsString();
                        thumbnail = object.get("thumbnail").getAsString().replace("\\", "");
                        video_index = Integer.parseInt(object.get("id").getAsString());
                        likes = Integer.parseInt(object.get("likes").getAsString());
                        dislikes = Integer.parseInt(object.get("dislikes").getAsString());

                        if (video_kind.equals("YOUTUBE")) {
                            video_id = cateDetail.substring(cateDetail.indexOf("=") + 1);
                        }
                        if (video_kind.equals("TWITCH")) {
                            String[] split = cateDetail.split("/");
                            video_id = split[split.length - 1];
                        }

                        youtubeObject.setVideo_index(video_index);
                        youtubeObject.setTitle(cateName);
                        youtubeObject.setThumbnail(thumbnail);
                        youtubeObject.setVideo_id(video_id);
                        youtubeObject.setVideo_kind(video_kind);
                        youtubeObject.setLikes(likes);
                        youtubeObject.setDislikes(dislikes);

                        mListData.add(youtubeObject);
//                        initList(mListData);
                    } new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    },1000);
                } catch(IndexOutOfBoundsException ea){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
//                            Toast.makeText(getContext(), "더이상 동영상이 없습니다.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    },1000);
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    //가로 카테고리
    private void init(ArrayList<String> arrayList,final int start) {

        adapter2 = new HorizontalCategoryAdapter(getContext(), arrayList, new OnArrayClickListner(){
            @Override
            public void onArrayClick(String category, View view) {
                if(preView != null) preView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border));
                preView = view;
                progressBarstart.setVisibility(view.VISIBLE);
                view.setBackgroundColor(Color.LTGRAY);

                category_selected=category;
                mListData = new ArrayList<>();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RetrofitService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                retrofitService.getCategoryVideo(category, sortby).enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        try {
                            for (int i = start; i < start + 3; i++) {
                                JsonObject object = response.body().get(i).getAsJsonObject();

                                YoutubeDataModel youtubeObject = new YoutubeDataModel();
                                String thumbnail = "";
                                String video_id = "";
                                String cateName, video_kind, cateDetail;
                                int video_index, likes, dislikes;

                                cateName = object.get("title").getAsString();
                                video_kind = object.get("kind").getAsString();
                                cateDetail = object.get("url").getAsString();
                                thumbnail = object.get("thumbnail").getAsString().replace("\\", "");
                                video_index = Integer.parseInt(object.get("id").getAsString());
                                likes = Integer.parseInt(object.get("likes").getAsString());
                                dislikes = Integer.parseInt(object.get("dislikes").getAsString());

                                if (video_kind.equals("YOUTUBE")) {
                                    video_id = cateDetail.substring(cateDetail.indexOf("=") + 1);
                                }
                                if (video_kind.equals("TWITCH")) {
                                    String[] split = cateDetail.split("/");
                                    video_id = split[split.length - 1];
                                }

                                youtubeObject.setVideo_index(video_index);
                                youtubeObject.setTitle(cateName);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);
                                youtubeObject.setVideo_kind(video_kind);
                                youtubeObject.setLikes(likes);
                                youtubeObject.setDislikes(dislikes);

                                mListData.add(youtubeObject);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (start == 0) {
                                        initList(mListData);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    progressBarstart.setVisibility(View.GONE);
                                }
                            }, 1000);
                        } catch (IndexOutOfBoundsException ea) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (start == 0) {
                                        initList(mListData);
                                    } else {
                                        adapter.notifyDataSetChanged();
//                                        Toast.makeText(getContext(), "더이상 동영상이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    progressBarstart.setVisibility(View.GONE);
                                }
                            }, 1000);
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {

                    }
                });
            }
        });
        listview2.setAdapter(adapter2);
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        progressBar.setVisibility(View.GONE);

        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                final YoutubeDataModel youtubeDataModel = item;
                if (youtubeDataModel.getVideo_kind().equals("YOUTUBE")) { //유튜브 플레이어
                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl(RetrofitService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                    Call<JsonObject> call=retrofitService.MakeLikeTable(MainActivity.strName,youtubeDataModel.getVideo_index());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject jsonObject = response.body();
                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                            intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                            intent.putExtra("u_v_status", jsonObject.get("status").getAsInt());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                }
                if (youtubeDataModel.getVideo_kind().equals("TWITCH")) {
                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl(RetrofitService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                    Call<JsonObject> call=retrofitService.MakeLikeTable(MainActivity.strName,youtubeDataModel.getVideo_index());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject jsonObject=response.body();
                            Intent intent = new Intent(getActivity(), TwitchActivity.class);
                            intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                            intent.putExtra("u_v_status", jsonObject.get("status").getAsInt());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                }
            }
        });
        mList_videos.setAdapter(adapter);
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}