package hr.eazework.com.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 04-12-2018.
 */

public class GetQuickHelpSearchResultModel extends GenericResponse
        implements Serializable {
    private String HomeLink;
    private ArrayList<QuickHelpListModel> QuickHelpList;

    public ArrayList<QuickHelpListModel> getQuickHelpList() {
        return QuickHelpList;
    }

    public String getHomeLink() {
        return HomeLink;
    }

    public void setHomeLink(String homeLink) {
        HomeLink = homeLink;
    }

    public void setQuickHelpList(ArrayList<QuickHelpListModel> quickHelpList) {
        QuickHelpList = quickHelpList;
    }
}
