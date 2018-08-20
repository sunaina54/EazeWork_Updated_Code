package hr.eazework.com.model;

import java.io.Serializable;

/**
 * Created by SUNAINA on 17-08-2018.
 */

public class AnnouncementItemsModel extends GenericResponse implements Serializable {
    private String Desc;
    private String FilePath;
    private String Type;
    private Scale Scale;
    private int imageCorner;
    private int id;

    public AnnouncementItemsModel(int id,String filePath, int imageCorner) {
        FilePath = filePath;
        this.imageCorner = imageCorner;
        this.id = id;
    }

    public int getImageCorner() {
        return imageCorner;
    }

    public void setImageCorner(int imageCorner) {
        this.imageCorner = imageCorner;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public hr.eazework.com.model.Scale getScale() {
        return Scale;
    }

    public void setScale(hr.eazework.com.model.Scale scale) {
        Scale = scale;
    }
}
