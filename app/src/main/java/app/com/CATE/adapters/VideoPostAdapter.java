package app.com.CATE.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.youtubeapiv3.R;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.models.YoutubeDataModel;

/**
 * Created by mdmunirhossain on 12/18/17.
 */

public class VideoPostAdapter extends RecyclerView.Adapter<VideoPostAdapter.YoutubePostHolder> {

    private ArrayList<YoutubeDataModel> dataSet;
    private Context mContext = null;
    private final OnItemClickListener listener;
    private int mode;

    public VideoPostAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        this.listener = listener;
    }

    public VideoPostAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet, int mode, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        this.listener = listener;
        this.mode = mode;
    }

    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_post_layout,parent,false);
        YoutubePostHolder postHolder = new YoutubePostHolder(view);
        return postHolder;
    }

    @Override
    public void onBindViewHolder(YoutubePostHolder holder, int position) {

        //set the views here
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewDes = holder.textViewDes;
        TextView textViewDate = holder.textViewDate;
        ImageView ImageThumb = holder.ImageThumb;

        YoutubeDataModel object = dataSet.get(position);

        textViewTitle.setText(object.getTitle());
        textViewDes.setText(object.getDescription());
        textViewDate.setText(object.getPublishedAt());
        holder.bind(dataSet.get(position), listener);

        //TODO: image will be downloaded from url
        Picasso.with(mContext).load(object.getThumbnail()).into(ImageThumb);

        if(mode == 1){
            LinearLayout youtube_post_layout = holder.youtube_post_layout;
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) youtube_post_layout.getLayoutParams();
            lp.height = 500;
            lp.width = 750;
            youtube_post_layout.setLayoutParams(lp);

            textViewTitle.setTextSize(20);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class YoutubePostHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDes;
        TextView textViewDate;
        ImageView ImageThumb;
        LinearLayout youtube_post_layout;

        public YoutubePostHolder(View itemView) {
            super(itemView);
            this.textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.textViewDes = (TextView) itemView.findViewById(R.id.textViewDes);
            this.textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            this.ImageThumb = (ImageView) itemView.findViewById(R.id.ImageThumb);
            this.youtube_post_layout = (LinearLayout) itemView.findViewById(R.id.youtube_post_layout);
        }

        public void bind(final YoutubeDataModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
