package hr.eazework.com.model;

import java.io.Serializable;

/**
 * Created by SUNAINA on 17-08-2018.
 */

public class Scale implements Serializable {
    private String height;
    private String width;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
