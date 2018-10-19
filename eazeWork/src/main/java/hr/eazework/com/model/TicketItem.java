package hr.eazework.com.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 05-10-2018.
 */

public class TicketItem extends GenericResponse implements Serializable {

    private String CategoryID;
    private String Comment;
    private String CustomerCorpID;
    private String CustomerEmpID;
    private String CustomerEmpName;
    private ArrayList<DocListModel> Files;
    private String FromButton;
    private String Remarks;
    private String Response;
    private String SimpleOrAdvance;
    private String Status;
    private String SubCategoryID;
    private String Subject;
    private String TicketCode;
    private String TicketID;
    private String TicketPriorityID;
    private String TicketTypeID;
    private String Date;
    private String Description;
    private String Priority;
    private String StatusDesc;
    private String PendingWith;
    private String[] Buttons;

    private String CategoryDesc="";
    private String TicketPriorityDesc="";
    private String TicketTypeDesc="";
    private String SubCategoryDesc="";

    public String getCustomerEmpName() {
        return CustomerEmpName;
    }

    public void setCustomerEmpName(String customerEmpName) {
        CustomerEmpName = customerEmpName;
    }

    /* {
        "ErrorCode": 0,
            "ErrorMessage": "",
            "MessageType": "N",
            "CategoryID": null,
            "Comment": null,
            "CustomerCorpID": null,
            "CustomerEmpID": null,
            "Date": "30-Jan-2015 04:45",
            "Description": "Manoj1 Kumar-For Feedback alert testing-30/01/2015",
            "DocList": null,
            "FromButton": null,
            "NewRemark": null,
            "PendingWith": "Akash Sharma",
            "Priority": "Critical",
            "Remarks": null,
            "Response": null,
            "SimpleOrAdvance": "A",
            "Status": 0,
            "StatusDesc": "Feedback Completed",
            "SubCategoryID": null,
            "Subject": "For Feedback alert testing",
            "TicketCode": "T00269",
            "TicketID": "nvp8AwdSmVEcMccizptHBQ==",
            "TicketPriorityID": 0,
            "TicketTypeID": 0
    }*/

    public String getCategoryDesc() {
        return CategoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        CategoryDesc = categoryDesc;
    }

    public String getTicketPriorityDesc() {
        return TicketPriorityDesc;
    }

    public void setTicketPriorityDesc(String ticketPriorityDesc) {
        TicketPriorityDesc = ticketPriorityDesc;
    }

    public String getTicketTypeDesc() {
        return TicketTypeDesc;
    }

    public void setTicketTypeDesc(String ticketTypeDesc) {
        TicketTypeDesc = ticketTypeDesc;
    }

    public String getSubCategoryDesc() {
        return SubCategoryDesc;
    }

    public void setSubCategoryDesc(String subCategoryDesc) {
        SubCategoryDesc = subCategoryDesc;
    }

    public String[] getButtons() {
        return Buttons;
    }

    public void setButtons(String[] buttons) {
        Buttons = buttons;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getStatusDesc() {
        return StatusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        StatusDesc = statusDesc;
    }

    public String getPendingWith() {
        return PendingWith;
    }

    public void setPendingWith(String pendingWith) {
        PendingWith = pendingWith;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
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

    public ArrayList<DocListModel> getFiles() {
        return Files;
    }

    public void setFiles(ArrayList<DocListModel> files) {
        Files = files;
    }

    public String getFromButton() {
        return FromButton;
    }

    public void setFromButton(String fromButton) {
        FromButton = fromButton;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public String getSimpleOrAdvance() {
        return SimpleOrAdvance;
    }

    public void setSimpleOrAdvance(String simpleOrAdvance) {
        SimpleOrAdvance = simpleOrAdvance;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public String getTicketCode() {
        return TicketCode;
    }

    public void setTicketCode(String ticketCode) {
        TicketCode = ticketCode;
    }

    public String getTicketID() {
        return TicketID;
    }

    public void setTicketID(String ticketID) {
        TicketID = ticketID;
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


    /*
                        "CategoryID": null,
                        "Comment": null,
                        "CustomerCorpID": null,
                        "CustomerEmpID": null,
                        "Files": null,
                        "FromButton": null,
                        "Remarks": null,
                        "Response": null,
                        "SimpleOrAdvance": "A",
                        "Status": 0,
                        "SubCategoryID": null,
                        "Subject": "I don't have time to test this, but perhaps you can bypass your problem by  mounting the desired directory higher in the heirarchy?  I don't know if  the underlying character counts count against the limit, but it's worth a  try. You are right in that NT ",
                        "TicketCode": "T00090",
                        "TicketID": "9W81sqY0hWh8WYxrzLG38A==",
                        "TicketPriorityID": 0,
                        "TicketTypeID": 0*/

    /*          "Buttons": null,
              "CategoryDesc": null,
              "CategoryID": null,
              "Comment": null,
              "CustomerCorpID": null,
              "CustomerEmpID": null,
              "CustomerEmpName": null,
              "Date": null,
              "Description": null,
              "DocList": null,
              "FromButton": null,
              "NewRemark": null,
              "PendingWith": null,
              "Remarks": null,
              "Response": null,
              "SimpleOrAdvance": "S",
              "Status": 2,
              "StatusDesc": null,
              "SubCategoryDesc": null,
              "SubCategoryID": null,
              "Subject": "sub 101",
              "TicketCode": "Q000009",
              "TicketID": "KKFJNdx+yFa9+DCiqw8iVw==",
              "TicketPriorityDesc": null,
              "TicketPriorityID": 0,
              "TicketTypeDesc": null,
              "TicketTypeID": 0*/


}
