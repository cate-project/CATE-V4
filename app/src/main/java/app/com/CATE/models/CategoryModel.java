package app.com.CATE.models;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mdmunirhossain on 12/18/17.
 */
public class CategoryModel{
    private String id ;
    private String name ;
    private String detail ;
    private String key ;
    private Boolean state;

    public void setId(String id) {
        id = id ;
    }
    public void setName(String name) {
        name = name ;
    }
    public void setDetail(String detail) {
        detail = detail ;
    }
    public void setKey(String key){
        key = key;
    }
    public void setState(Boolean state){
        state = state;
    }

    public String getId() {
        return this.id ;
    }
    public String getName() {
        return this.name ;
    }
    public String getDetail() {
        return this.detail ;
    }
    public String getKey(){
        return this.key;
    }
    public Boolean getState(){
        return this.state;
    }

    public CategoryModel(String id, String name, String detail, String key, Boolean state) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.key = key;
        this.state = state;
    }

}
