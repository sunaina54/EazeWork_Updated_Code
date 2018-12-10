package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;



/**
 * Created by SUNAINA on 16-08-2018.
 */

public class GetAnnouncementResult extends GenericResponse implements Serializable {
private ArrayList<AnnouncementItemsModel> AnnouncementItems=null;

    public ArrayList<AnnouncementItemsModel> getAnnouncementItems() {
        return AnnouncementItems;
    }

    public void setAnnouncementItems(ArrayList<AnnouncementItemsModel> announcementItems) {
        AnnouncementItems = announcementItems;
    }

}
