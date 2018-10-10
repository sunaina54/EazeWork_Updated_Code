package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 28-09-2018.
 */

public class GetTicketPageInitResultModel extends GenericResponse
        implements Serializable {

    private String SimpleOrAdvanceView;
    private CategoryList CategoryList;
    private TicketTypeList TicketTypeList;
    private FeedbackRatingListModel FeedbackRatingList;
    private PriorityListModel PriorityList;
    private TicketDetailModel TicketDetail;
    private ArrayList<ContactListModel> ContactList;
    private String[] Buttons;

    public hr.eazework.com.model.TicketTypeList getTicketTypeList() {
        return TicketTypeList;
    }

    public void setTicketTypeList(hr.eazework.com.model.TicketTypeList ticketTypeList) {
        TicketTypeList = ticketTypeList;
    }

    public String[] getButtons() {
        return Buttons;
    }

    public void setButtons(String[] buttons) {
        Buttons = buttons;
    }

    public String getSimpleOrAdvanceView() {
        return SimpleOrAdvanceView;
    }

    public void setSimpleOrAdvanceView(String simpleOrAdvanceView) {
        SimpleOrAdvanceView = simpleOrAdvanceView;
    }

    public hr.eazework.com.model.CategoryList getCategoryList() {
        return CategoryList;
    }

    public void setCategoryList(hr.eazework.com.model.CategoryList categoryList) {
        CategoryList = categoryList;
    }

    public FeedbackRatingListModel getFeedbackRatingList() {
        return FeedbackRatingList;
    }

    public void setFeedbackRatingList(FeedbackRatingListModel feedbackRatingList) {
        FeedbackRatingList = feedbackRatingList;
    }

    public PriorityListModel getPriorityList() {
        return PriorityList;
    }

    public void setPriorityList(PriorityListModel priorityList) {
        PriorityList = priorityList;
    }

    public TicketDetailModel getTicketDetail() {
        return TicketDetail;
    }

    public void setTicketDetail(TicketDetailModel ticketDetail) {
        TicketDetail = ticketDetail;
    }

    public ArrayList<ContactListModel> getContactList() {
        return ContactList;
    }

    public void setContactList(ArrayList<ContactListModel> contactList) {
        ContactList = contactList;
    }

    static public GetTicketPageInitResultModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetTicketPageInitResultModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
