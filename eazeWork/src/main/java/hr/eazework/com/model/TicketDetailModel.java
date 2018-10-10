package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 28-09-2018.
 */

public class TicketDetailModel implements Serializable {

  private String FromButton;
  private String TicketID;
  private String CustomerCorpID;
  private String CustomerEmpID;
  private String TicketPriorityID;
  private String TicketTypeID;
  private String CategoryID;
  private String SubCategoryID;
  private String Subject;
  private String Comment;
  private String NewRemark;
  private ArrayList<SupportDocsItemModel> DocList;

    static public TicketDetailModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TicketDetailModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getFromButton() {
        return FromButton;
    }

    public void setFromButton(String fromButton) {
        FromButton = fromButton;
    }

    public String getTicketID() {
        return TicketID;
    }

    public void setTicketID(String ticketID) {
        TicketID = ticketID;
    }

    public String getCustomerCorpID() {
        return CustomerCorpID;
    }

    public void setCustomerCorpID(String customerCorpID) {
        CustomerCorpID = customerCorpID;
    }

    public String getCustomerEmpID() {
        return CustomerEmpID;
    }

    public void setCustomerEmpID(String customerEmpID) {
        CustomerEmpID = customerEmpID;
    }

    public String getTicketPriorityID() {
        return TicketPriorityID;
    }

    public void setTicketPriorityID(String ticketPriorityID) {
        TicketPriorityID = ticketPriorityID;
    }

    public String getTicketTypeID() {
        return TicketTypeID;
    }

    public void setTicketTypeID(String ticketTypeID) {
        TicketTypeID = ticketTypeID;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getSubCategoryID() {
        return SubCategoryID;
    }

    public void setSubCategoryID(String subCategoryID) {
        SubCategoryID = subCategoryID;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getNewRemark() {
        return NewRemark;
    }

    public void setNewRemark(String newRemark) {
        NewRemark = newRemark;
    }

    public ArrayList<SupportDocsItemModel> getDocList() {
        return DocList;
    }

    public void setDocList(ArrayList<SupportDocsItemModel> docList) {
        DocList = docList;
    }
}

