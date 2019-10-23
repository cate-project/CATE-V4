package app.com.CATE.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import app.com.CATE.DetailsActivity;
import app.com.CATE.GMailSender;
import app.com.CATE.MainActivity;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.models.CommentModel;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_Normal = 0 ;
    private static final int ITEM_VIEW_TYPE_Best = 1 ;
    private static final int ITEM_VIEW_TYPE_MAX = 2 ;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<CommentModel> commentList = new ArrayList<CommentModel>() ;
    private Context mContext;
    ImageView delete_comment;

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }
    @Override
    public int getItemViewType(int position) {
        return commentList.get(position).getType() ;
    }
    // 생성자
    public CommentAdapter() {
    }

    public CommentAdapter(ArrayList<CommentModel> commentList,Context context){
        this.commentList = commentList;
        this.mContext = context;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return commentList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int viewType=getItemViewType(position);
        final int pos = position;
        final Context context = parent.getContext();
        final CommentModel commentModel = commentList.get(position);


        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_comment_layout, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
//        final TextView textAuthor = (TextView) convertView.findViewById(R.id.textAuthor) ;
//        TextView textDesc = (TextView) convertView.findViewById(R.id.textDesc) ;
//        TextView textdate = (TextView) convertView.findViewById(R.id.textdate) ;
//        final ImageView commentLike=(ImageView) convertView.findViewById(R.id.commentLike);
//        final ImageView commentDisLike=(ImageView) convertView.findViewById(R.id.commentDisLike);
//        delete_comment=(ImageView) convertView.findViewById(R.id.delete_comment);
//        final TextView declaration=(TextView) convertView.findViewById(R.id.declaration);
//        final TextView  commentcountLike = (TextView) convertView.findViewById(R.id.commentcountLike) ;
//        final TextView commentcountDisLike = (TextView) convertView.findViewById(R.id.commentcountDisLike);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_comment_layout, parent, false);
        final TextView textAuthor = convertView.findViewById(R.id.textAuthor);
        TextView textDesc = convertView.findViewById(R.id.textDesc);
        TextView textdate = convertView.findViewById(R.id.textDate);
        final ImageView commentLike= convertView.findViewById(R.id.imageCommentLike);
        final ImageView commentDisLike= convertView.findViewById(R.id.commentDisLike);
        delete_comment= convertView.findViewById(R.id.imageDeleteComment);
        final TextView declaration= convertView.findViewById(R.id.declaration);
        final TextView  commentcountLike = convertView.findViewById(R.id.textCommentCountLike);
        final TextView commentcountDisLike = convertView.findViewById(R.id.textCommentCountDisLike);

        switch (commentModel.getType()){
            case 1:     // 베스트 댓글
                convertView.setBackgroundResource(R.drawable.bestcomment);

            case 0:   // 베스트 댓글이 아닌 댓글

                if(commentModel.getStatus().equalsIgnoreCase("0")){
                    commentLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    commentLike.setTag(R.drawable.ic_thumb_up_24px);
                    commentDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                    commentDisLike.setTag(R.drawable.ic_thumb_down_24px);
                }
                else if(commentModel.getStatus().equalsIgnoreCase("1")){
                    commentLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);
                    commentLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                    commentDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                    commentDisLike.setTag(R.drawable.ic_thumb_down_24px);
                }
                else{
                    commentLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    commentLike.setTag(R.drawable.ic_thumb_up_24px);
                    commentDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);
                    commentDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                }

                commentLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( commentLike.getTag().equals(R.drawable.ic_thumb_up_selected_24px)) {     //좋아요 취소
                            update_likes(commentModel.get_index(),5);
                            commentLike.setImageResource(R.drawable.ic_thumb_up_24px);
                            commentLike.setTag(R.drawable.ic_thumb_up_24px);
                            commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString()) - 1));
                        }
                        else if(commentLike.getTag().equals(R.drawable.ic_thumb_up_24px) &&commentDisLike.getTag().equals(R.drawable.ic_thumb_down_selected_24px)){
                            update_likes(commentModel.get_index(),3);
                            commentLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);
                            commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString())+1));
                            commentDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                            commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())-1));
                            commentLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                            commentDisLike.setTag(R.drawable.ic_thumb_down_24px);
                        }
                        else{
                            update_likes(commentModel.get_index(),1);
                            commentLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);      //좋아요 누르기
                            commentLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                            commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString())+1));
                        }
                    }
                });

                commentDisLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ( commentDisLike.getTag().equals(R.drawable.ic_thumb_down_selected_24px) ){    //싫어요 취소
                            update_likes(commentModel.get_index(),6);
                            commentDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                            commentDisLike.setTag(R.drawable.ic_thumb_down_24px);
                            commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())-1));
                        }
                        else if(commentDisLike.getTag().equals(R.drawable.ic_thumb_down_24px) &&commentLike.getTag().equals(R.drawable.ic_thumb_up_selected_24px)){
                            update_likes(commentModel.get_index(),4);
                            commentLike.setImageResource(R.drawable.ic_thumb_up_24px);
                            commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString()) - 1));
                            commentDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);
                            commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())+1));
                            commentLike.setTag(R.drawable.ic_thumb_up_24px);
                            commentDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                        }
                        else{             //i가 1일때 싫어요 클릭이 안된 상태
                            update_likes(commentModel.get_index(),2);
                            commentDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);      //싫어요 누르기
                            commentDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                            commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())+1));
                        }
                    }
                });
                declaration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        final EditText edittext = new EditText(mContext);

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("신고하기");
                        builder.setMessage("신고 사유를 적어주세요.");
                        builder.setView(edittext);
                        builder.setPositiveButton("입력",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Thread t=new Thread(){
                                            @Override
                                            public void run() {
                                                try {
                                                    final ProgressDialog pd=ProgressDialog.show(mContext,"","신고 내용을 보내는 중입니다.");
                                                    GMailSender gMailSender = new GMailSender("ghkdua059@gmail.com", "6013861z!");
                                                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                                                    gMailSender.sendMail("게시글 번호 : "+commentModel.getVideo_id()+" 댓글번호 : "
                                                                    +commentModel.get_index()+" 작성자 : "+commentModel.getAuthor()+" 를(을) 신고합니다.",
                                                            "작성내용 : "+commentModel.getDesc()+"\n신고 이유 : "+edittext.getText().toString(), "ghkdua1829@naver.com");
                                                    Looper.prepare();
                                                    Toast.makeText(mContext, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                } catch (SendFailedException e) {
                                                    Toast.makeText(mContext, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                                } catch (MessagingException e) {
                                                    Toast.makeText(mContext, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                Looper.loop();
                                            }
                                        };
                                        t.start();
                                    }
                                });
                        builder.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();

                    }
                });


                // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
                // 아이템 내 각 위젯에 데이터 반영
                if(MainActivity.strName.equalsIgnoreCase(commentModel.getAuthor())){
                    Log.e("ss : ",commentModel.getAuthor());
                    delete_comment.setVisibility(View.VISIBLE);
                }
                else{
                    delete_comment.setVisibility(View.INVISIBLE);
                }
                delete_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Retrofit retrofit=new Retrofit.Builder()
                                .baseUrl(RetrofitService.URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                        Call<JsonObject> call=retrofitService.DeleteComment(Integer.parseInt(commentModel.getVideo_id()),
                                Integer.parseInt(commentModel.get_index()),MainActivity.strName);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Toast.makeText(mContext, "정상적으로 삭제가 되었습니다.", Toast.LENGTH_SHORT).show();
                                commentList.remove(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
                    }
                });
                textAuthor.setText(commentModel.getAuthor());
                textDesc.setText(commentModel.getDesc());
                textdate.setText(commentModel.getDate());
                commentcountLike.setText(commentModel.getCommentcountLike());
                commentcountDisLike.setText(commentModel.getCommentcountDisLike());
                break;

//            case 1:  //베스트 댓글
//
//                LayoutInflater inflaterB = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = inflaterB.inflate(R.layout.activity_comment_best, parent, false);
//                final TextView textAuthorB = (TextView) convertView.findViewById(R.id.textAuthorB) ;
//                TextView textDescB = (TextView) convertView.findViewById(R.id.textDescB) ;
//                TextView textdateB = (TextView) convertView.findViewById(R.id.textdateB) ;
//                final ImageView commentLikeB=(ImageView) convertView.findViewById(R.id.commentLikeB);
//                final ImageView commentDisLikeB=(ImageView) convertView.findViewById(R.id.commentDisLikeB);
//                delete_comment=(ImageView) convertView.findViewById(R.id.delete_commentB);
//                final TextView declarationB=(TextView) convertView.findViewById(R.id.declarationB);
//                final TextView  commentcountLikeB = (TextView) convertView.findViewById(R.id.commentcountLikeB) ;
//                final TextView commentcountDisLikeB = (TextView) convertView.findViewById(R.id.commentcountDisLikeB);
//
//                if(commentModel.getStatus().equalsIgnoreCase("0")){
//                    commentLikeB.setImageResource(R.drawable.ic_thumb_up);
//                    commentLikeB.setTag(R.drawable.ic_thumb_up);
//                    commentDisLikeB.setImageResource(R.drawable.ic_thumb_down);
//                    commentDisLikeB.setTag(R.drawable.ic_thumb_down);
//                }
//                else if(commentModel.getStatus().equalsIgnoreCase("1")){
//                    commentLikeB.setImageResource(R.drawable.ic_thumb_up_selected);
//                    commentLikeB.setTag(R.drawable.ic_thumb_up_selected);
//                    commentDisLikeB.setImageResource(R.drawable.ic_thumb_down);
//                    commentDisLikeB.setTag(R.drawable.ic_thumb_down);
//                }
//                else{
//                    commentLikeB.setImageResource(R.drawable.ic_thumb_up);
//                    commentLikeB.setTag(R.drawable.ic_thumb_up);
//                    commentDisLikeB.setImageResource(R.drawable.ic_thumb_down_selected_24px);
//                    commentDisLikeB.setTag(R.drawable.ic_thumb_down_selected_24px);
//                }
//
//                commentLikeB.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if ( commentLikeB.getTag().equals(R.drawable.ic_thumb_up_selected)) {     //좋아요 취소
//                            update_likes(commentModel.get_index(),5);
//                            commentLikeB.setImageResource(R.drawable.ic_thumb_up);
//                            commentLikeB.setTag(R.drawable.ic_thumb_up);
//                            commentcountLikeB.setText(String.valueOf(Integer.parseInt(commentcountLikeB.getText().toString()) - 1));
//                        }
//                        else if(commentLikeB.getTag().equals(R.drawable.ic_thumb_up) &&commentDisLikeB.getTag().equals(R.drawable.ic_thumb_down_selected)){
//                            update_likes(commentModel.get_index(),3);
//                            commentLikeB.setImageResource(R.drawable.ic_thumb_up_selected);
//                            commentcountLikeB.setText(String.valueOf(Integer.parseInt(commentcountLikeB.getText().toString())+1));
//                            commentDisLikeB.setImageResource(R.drawable.ic_thumb_down);
//                            commentcountDisLikeB.setText(String.valueOf(Integer.parseInt(commentcountDisLikeB.getText().toString())-1));
//                            commentLikeB.setTag(R.drawable.ic_thumb_up_selected);
//                            commentDisLikeB.setTag(R.drawable.ic_thumb_down);
//                        }
//                        else{
//                            update_likes(commentModel.get_index(),1);
//                            commentLikeB.setImageResource(R.drawable.ic_thumb_up_selected);      //좋아요 누르기
//                            commentLikeB.setTag(R.drawable.ic_thumb_up_selected);
//                            commentcountLikeB.setText(String.valueOf(Integer.parseInt(commentcountLikeB.getText().toString())+1));
//                        }
//                    }
//                });
//
//                commentDisLikeB.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        if ( commentDisLikeB.getTag().equals(R.drawable.ic_thumb_down_selected) ){    //싫어요 취소
//                            update_likes(commentModel.get_index(),6);
//                            commentDisLikeB.setImageResource(R.drawable.ic_thumb_down);
//                            commentDisLikeB.setTag(R.drawable.ic_thumb_down);
//                            commentcountDisLikeB.setText(String.valueOf(Integer.parseInt(commentcountDisLikeB.getText().toString())-1));
//                        }
//                        else if(commentDisLikeB.getTag().equals(R.drawable.ic_thumb_down) &&commentLikeB.getTag().equals(R.drawable.ic_thumb_up_selected)){
//                            update_likes(commentModel.get_index(),4);
//                            commentLikeB.setImageResource(R.drawable.ic_thumb_up);
//                            commentcountLikeB.setText(String.valueOf(Integer.parseInt(commentcountLikeB.getText().toString()) - 1));
//                            commentDisLikeB.setImageResource(R.drawable.ic_thumb_down_selected);
//                            commentcountDisLikeB.setText(String.valueOf(Integer.parseInt(commentcountDisLikeB.getText().toString())+1));
//                            commentLikeB.setTag(R.drawable.ic_thumb_up);
//                            commentDisLikeB.setTag(R.drawable.ic_thumb_down_selected);
//                        }
//                        else{             //i가 1일때 싫어요 클릭이 안된 상태
//                            update_likes(commentModel.get_index(),2);
//                            commentDisLikeB.setImageResource(R.drawable.ic_thumb_down_selected);      //싫어요 누르기
//                            commentDisLikeB.setTag(R.drawable.ic_thumb_down_selected);
//                            commentcountDisLikeB.setText(String.valueOf(Integer.parseInt(commentcountDisLikeB.getText().toString())+1));
//                        }
//                    }
//                });
//                declarationB.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//                        final EditText edittext = new EditText(mContext);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setTitle("신고하기");
//                        builder.setMessage("신고 사유를 적어주세요.");
//                        builder.setView(edittext);
//                        builder.setPositiveButton("입력",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Thread t=new Thread(){
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    final ProgressDialog pd=ProgressDialog.show(mContext,"","신고 내용을 보내는 중입니다.");
//                                                    GMailSender gMailSender = new GMailSender("ghkdua059@gmail.com", "6013861z!");
//                                                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
//                                                    gMailSender.sendMail("게시글 번호 : "+commentModel.getVideo_id()+" 댓글번호 : "
//                                                                    +commentModel.get_index()+" 작성자 : "+commentModel.getAuthor()+" 를(을) 신고합니다.",
//                                                            "작성내용 : "+commentModel.getDesc()+"\n신고 이유 : "+edittext.getText().toString(), "ghkdua1829@naver.com");
//                                                    Looper.prepare();
//                                                    Toast.makeText(mContext, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
//                                                    pd.dismiss();
//                                                } catch (SendFailedException e) {
//                                                    Toast.makeText(mContext, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
//                                                } catch (MessagingException e) {
//                                                    Toast.makeText(mContext, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
//                                                    e.printStackTrace();
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                }
//                                                Looper.loop();
//                                            }
//                                        };
//                                        t.start();
//                                    }
//                                });
//                        builder.setNegativeButton("취소",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                        builder.show();
//
//                    }
//                });
//
//
//                // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
//                // 아이템 내 각 위젯에 데이터 반영
//                if(MainActivity.strName.equalsIgnoreCase(commentModel.getAuthor())){
//                    Log.e("ss : ",commentModel.getAuthor());
//                    delete_comment.setVisibility(View.VISIBLE);
//                }
//                else{
//                    delete_comment.setVisibility(View.INVISIBLE);
//                }
//                delete_comment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Retrofit retrofit=new Retrofit.Builder()
//                                .baseUrl(RetrofitService.URL)
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build();
//                        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
//                        Call<JsonObject> call=retrofitService.DeleteComment(Integer.parseInt(commentModel.getVideo_id()),
//                                Integer.parseInt(commentModel.get_index()),MainActivity.strName);
//                        call.enqueue(new Callback<JsonObject>() {
//                            @Override
//                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                                Toast.makeText(mContext, "정상적으로 삭제가 되었습니다.", Toast.LENGTH_SHORT).show();
//                                commentList.remove(position);
//                                notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                });
//                textAuthorB.setText(commentModel.getAuthor());
//                textDescB.setText(commentModel.getDesc());
//                textdateB.setText(commentModel.getDate());
//                commentcountLikeB.setText(commentModel.getCommentcountLike());
//                commentcountDisLikeB.setText(commentModel.getCommentcountDisLike());
//                break;
        }

//        if(commentModel.getStatus().equalsIgnoreCase("0")){
//            commentLike.setImageResource(R.drawable.ic_thumb_up);
//            commentLike.setTag(R.drawable.ic_thumb_up);
//            commentDisLike.setImageResource(R.drawable.ic_thumb_down);
//            commentDisLike.setTag(R.drawable.ic_thumb_down);
//        }
//        else if(commentModel.getStatus().equalsIgnoreCase("1")){
//            commentLike.setImageResource(R.drawable.ic_thumb_up_selected);
//            commentLike.setTag(R.drawable.ic_thumb_up_selected);
//            commentDisLike.setImageResource(R.drawable.ic_thumb_down);
//            commentDisLike.setTag(R.drawable.ic_thumb_down);
//        }
//        else{
//            commentLike.setImageResource(R.drawable.ic_thumb_up);
//            commentLike.setTag(R.drawable.ic_thumb_up);
//            commentDisLike.setImageResource(R.drawable.ic_thumb_down_selected);
//            commentDisLike.setTag(R.drawable.ic_thumb_down_selected);
//        }
//
//        commentLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if ( commentLike.getTag().equals(R.drawable.ic_thumb_up_selected)) {     //좋아요 취소
//                    update_likes(commentModel.get_index(),5);
//                    commentLike.setImageResource(R.drawable.ic_thumb_up);
//                    commentLike.setTag(R.drawable.ic_thumb_up);
//                    commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString()) - 1));
//                }
//                else if(commentLike.getTag().equals(R.drawable.ic_thumb_up) &&commentDisLike.getTag().equals(R.drawable.ic_thumb_down_selected)){
//                    update_likes(commentModel.get_index(),3);
//                    commentLike.setImageResource(R.drawable.ic_thumb_up_selected);
//                    commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString())+1));
//                    commentDisLike.setImageResource(R.drawable.ic_thumb_down);
//                    commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())-1));
//                    commentLike.setTag(R.drawable.ic_thumb_up_selected);
//                    commentDisLike.setTag(R.drawable.ic_thumb_down);
//                }
//                else{
//                    update_likes(commentModel.get_index(),1);
//                    commentLike.setImageResource(R.drawable.ic_thumb_up_selected);      //좋아요 누르기
//                    commentLike.setTag(R.drawable.ic_thumb_up_selected);
//                    commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString())+1));
//                }
//            }
//        });
//
//        commentDisLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if ( commentDisLike.getTag().equals(R.drawable.ic_thumb_down_selected) ){    //싫어요 취소
//                    update_likes(commentModel.get_index(),6);
//                    commentDisLike.setImageResource(R.drawable.ic_thumb_down);
//                    commentDisLike.setTag(R.drawable.ic_thumb_down);
//                    commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())-1));
//                }
//                else if(commentDisLike.getTag().equals(R.drawable.ic_thumb_down) &&commentLike.getTag().equals(R.drawable.ic_thumb_up_selected)){
//                    update_likes(commentModel.get_index(),4);
//                    commentLike.setImageResource(R.drawable.ic_thumb_up);
//                    commentcountLike.setText(String.valueOf(Integer.parseInt(commentcountLike.getText().toString()) - 1));
//                    commentDisLike.setImageResource(R.drawable.ic_thumb_down_selected);
//                    commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())+1));
//                    commentLike.setTag(R.drawable.ic_thumb_up);
//                    commentDisLike.setTag(R.drawable.ic_thumb_down_selected);
//                }
//                else{             //i가 1일때 싫어요 클릭이 안된 상태
//                    update_likes(commentModel.get_index(),2);
//                    commentDisLike.setImageResource(R.drawable.ic_thumb_down_selected);      //싫어요 누르기
//                    commentDisLike.setTag(R.drawable.ic_thumb_down_selected);
//                    commentcountDisLike.setText(String.valueOf(Integer.parseInt(commentcountDisLike.getText().toString())+1));
//                }
//            }
//        });
//        declaration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                final EditText edittext = new EditText(mContext);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setTitle("신고하기");
//                builder.setMessage("신고 사유를 적어주세요.");
//                builder.setView(edittext);
//                builder.setPositiveButton("입력",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Thread t=new Thread(){
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            final ProgressDialog pd=ProgressDialog.show(mContext,"","신고 내용을 보내는 중입니다.");
//                                            GMailSender gMailSender = new GMailSender("ghkdua059@gmail.com", "6013861z!");
//                                            //GMailSender.sendMail(제목, 본문내용, 받는사람);
//                                            gMailSender.sendMail("게시글 번호 : "+commentModel.getVideo_id()+" 댓글번호 : "
//                                                            +commentModel.get_index()+" 작성자 : "+commentModel.getAuthor()+" 를(을) 신고합니다.",
//                                                    "작성내용 : "+commentModel.getDesc()+"\n신고 이유 : "+edittext.getText().toString(), "ghkdua1829@naver.com");
//                                            Looper.prepare();
//                                            Toast.makeText(mContext, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
//                                            pd.dismiss();
//                                        } catch (SendFailedException e) {
//                                            Toast.makeText(mContext, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
//                                        } catch (MessagingException e) {
//                                            Toast.makeText(mContext, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
//                                            e.printStackTrace();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        Looper.loop();
//                                    }
//                                };
//                                t.start();
//                            }
//                        });
//                builder.setNegativeButton("취소",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                builder.show();
//
//            }
//        });
//
//
//        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
//        // 아이템 내 각 위젯에 데이터 반영
//        if(MainActivity.strName.equalsIgnoreCase(commentModel.getAuthor())){
//            Log.e("ss : ",commentModel.getAuthor());
//            delete_comment.setVisibility(View.VISIBLE);
//        }
//        else{
//            delete_comment.setVisibility(View.INVISIBLE);
//        }
//        delete_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Retrofit retrofit=new Retrofit.Builder()
//                        .baseUrl(RetrofitService.URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//                RetrofitService retrofitService=retrofit.create(RetrofitService.class);
//                Call<JsonObject> call=retrofitService.DeleteComment(Integer.parseInt(commentModel.getVideo_id()),
//                        Integer.parseInt(commentModel.get_index()),MainActivity.strName);
//                call.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        Toast.makeText(mContext, "정상적으로 삭제가 되었습니다.", Toast.LENGTH_SHORT).show();
//                        commentList.remove(position);
//                        notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                    }
//                });
//            }
//        });
//        textAuthor.setText(commentModel.getAuthor());
//        textDesc.setText(commentModel.getDesc());
//        textdate.setText(commentModel.getDate());
//        commentcountLike.setText(commentModel.getCommentcountLike());
//        commentcountDisLike.setText(commentModel.getCommentcountDisLike());
        return convertView;
    }

    public void update_likes(String _index,final int target){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Call<JsonObject> call=retrofitService.updatecommentlikes(DetailsActivity.userName,String.valueOf(DetailsActivity.video_index),_index,String.valueOf(target));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Err", t.getMessage());
            }
        });
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return commentList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String author, String desc) {
        CommentModel item = new CommentModel();
        item.setType(ITEM_VIEW_TYPE_Best) ;
        item.setAuthor(author);
        item.setDesc(desc);
        item.set_index("10");
        item.setCommentcountDisLike("0");
        item.setCommentcountLike("0");
        item.setDate("sds");
        item.setVideo_id("8");
        item.setStatus("0");

        commentList.add(item);
    }
    class thread implements Runnable{
        @Override
        public void run() {
            try {
                GMailSender gMailSender = new GMailSender("ghkdua059@gmail.com", "6013861z!");
                //GMailSender.sendMail(제목, 본문내용, 받는사람);
                gMailSender.sendMail("제목입니다", "AAA", "ghkdua1829@naver.com");
                Toast.makeText(mContext, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (SendFailedException e) {
                Toast.makeText(mContext, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {
                Toast.makeText(mContext, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}