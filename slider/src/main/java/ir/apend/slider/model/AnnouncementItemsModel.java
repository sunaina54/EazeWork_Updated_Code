package ir.apend.slider.model;


import java.io.Serializable;

/**
 * Created by SUNAINA on 16-08-2018.
 */

public class AnnouncementItemsModel implements Serializable {
    private String ErrorCode;
    private String ErrorMessage;
    private String MessageType;
    private String Desc;
    private String FilePath;
    private String Type;
    private int imageCorner;
    private int id;
    private Scale Scale;

    public AnnouncementItemsModel( int id,String filePath, int imageCorner) {
        FilePath = filePath;
        this.imageCorner = imageCorner;
        this.id = id;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public ir.apend.slider.model.Scale getScale() {
        return Scale;
    }

    public void setScale(ir.apend.slider.model.Scale scale) {
        Scale = scale;
    }

    public int getImageCorner() {
        return imageCorner;
    }

    public void setImageCorner(int imageCorner) {
        this.imageCorner = imageCorner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


}
