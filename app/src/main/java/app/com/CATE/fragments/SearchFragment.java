package app.com.CATE.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import app.com.CATE.DetailsActivity;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.TwitchActivity;
import app.com.CATE.adapters.VideoPostAdapter;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.MainActivity;
import app.com.CATE.models.YoutubeDataModel;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    SearchView searchview;
    RecyclerView recyclerView;
    private VideoPostAdapter adapter = null;
    ArrayList<YoutubeDataModel> listData = new ArrayList<>();
    private ProgressBar progressBar;
    private MainActivity mainActivity;
    //
//
////
// public void onActivityCreated(@Nullable Bundle savedInstanceState) {
// super.onActivityCreated(savedInstanceState);
// LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,true);
// }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchview = (SearchView) view.findViewById(R.id.searching);
        recyclerView = (RecyclerView) view.findViewById(R.id.searchlist);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        searchview.setSubmitButtonEnabled(true);
        mainActivity = (MainActivity) getActivity();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount();
                Log.e("sdsd", "ssss : " + lastVisibleItemPosition);
                if (lastVisibleItemPosition == itemTotalCount - 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    //리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                    All_video(lastVisibleItemPosition + 1);
                }
            }
        });
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String target) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RetrofitService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                retrofitService.getSearchVideo(target).enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        ArrayList<YoutubeDataModel> listData = new ArrayList<>();

                        for (int i = 0; i < response.body().size(); i++) {
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

                            listData.add(youtubeObject);
                        }

                        initList(listData);
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {

                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

        All_video();

        return view;
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                final YoutubeDataModel youtubeDataModel = item;
                if (youtubeDataModel.getVideo_kind().equals("YOUTUBE")) { //유튜브 플레이어
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                    Call<JsonObject> call = retrofitService.MakeLikeTable(MainActivity.strName, youtubeDataModel.getVideo_index());
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
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                    Call<JsonObject> call = retrofitService.MakeLikeTable(MainActivity.strName, youtubeDataModel.getVideo_index());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject jsonObject = response.body();
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
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public void All_video() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<JsonObject> call = retrofitService.All_video(MainActivity.strName);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                try {
                    JsonArray jsonArray = jsonObject.get("response").getAsJsonArray();

                    int count = 0;

                    while (count < 3) {
                        JsonObject object = jsonArray.get(count).getAsJsonObject();

                        YoutubeDataModel youtubeObject = new YoutubeDataModel();
                        String thumbnail = "";
                        String video_id = "";
                        String cateName, video_kind, cateDetail;
                        int video_index, likes, dislikes;

                        cateName = object.get("title").getAsString();
                        video_kind = object.get("kind").getAsString();
                        cateDetail = object.get("url").getAsString();
                        thumbnail = object.get("thumbnail").getAsString();
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

                        count++;
                        listData.add(youtubeObject);
                    }
                    initList(listData);
//                    mainActivity.listData = listData;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void All_video(final int start) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<JsonObject> call = retrofitService.All_video(MainActivity.strName);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                try {
                    JsonArray jsonArray = jsonObject.get("response").getAsJsonArray();

                    int count = start;

                    while (count < start + 3) {
                        JsonObject object = jsonArray.get(count).getAsJsonObject();

                        YoutubeDataModel youtubeObject = new YoutubeDataModel();
                        String thumbnail = "";
                        String video_id = "";
                        String cateName, video_kind, cateDetail;
                        int video_index, likes, dislikes;

                        cateName = object.get("title").getAsString();
                        video_kind = object.get("kind").getAsString();
                        cateDetail = object.get("url").getAsString();
                        thumbnail = object.get("thumbnail").getAsString();
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

                        count++;
                        listData.add(youtubeObject);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 1000);
//                    initList(listData);
//                    mainActivity.listData = listData;
                } catch (IndexOutOfBoundsException ea) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "더이상 동영상이 없습니다.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}