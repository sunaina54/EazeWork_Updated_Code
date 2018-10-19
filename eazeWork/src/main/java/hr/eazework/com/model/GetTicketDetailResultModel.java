package hr.eazework.com.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by SUNAINA on 12-10-2018.
 */

public class GetTicketDetailResultModel extends GenericResponse {
    private String[] Buttons;
    private String CategoryID="";
    private String Comment="";
    private String CustomerCorpID="";
    private String CustomerEmpID="";
    private String Date="";
    private String Description="";
    private ArrayList<SupportDocsItemModel> DocList;
    private String FromButton="";
    private String NewRemark="";
    private String PendingWith="";
    private String Priority="";
    private ArrayList<RemarkListItem> Remarks;
    private String Response="";
    private String SimpleOrAdvance="";
    private String Status="";
    private String StatusDesc="";
    private String SubCategoryID="";
    private String Subject="";
    private String TicketCode="";
    private String TicketID="";
    private String TicketPriorityID="";
    private String TicketTypeID="";
    private String CategoryDesc="";
    private String TicketPriorityDesc="";
    private String TicketTypeDesc="";
    private String SubCategoryDesc="";
    private String CustomerEmpName="";



    static public GetTicketDetailResultModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetTicketDetailResultModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getCustomerEmpName() {
        return CustomerEmpName;
    }

    public void setCustomerEmpName(String customerEmpName) {
        CustomerEmpName = customerEmpName;
    }

    public String getSubCategoryDesc() {
        return SubCategoryDesc;
    }

    public void setSubCategoryDesc(String subCategoryDesc) {
        SubCategoryDesc = subCategoryDesc;
    }

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

    public String[] getButtons() {
        return Buttons;
    }

    public void setButtons(String[] buttons) {
        Buttons = buttons;
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

    public ArrayList<SupportDocsItemModel> getDocList() {
        return DocList;
    }

    public void setDocList(ArrayList<SupportDocsItemModel> docList) {
        DocList = docList;
    }

    public String getFromButton() {
        return FromButton;
    }

    public void setFromButton(String fromButton) {
        FromButton = fromButton;
    }

    public String getNewRemark() {
        return NewRemark;
    }

    public void setNewRemark(String newRemark) {
        NewRemark = newRemark;
    }

    public String getPendingWith() {
        return PendingWith;
    }

    public void setPendingWith(String pendingWith) {
        PendingWith = pendingWith;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public ArrayList<RemarkListItem> getRemarks() {
        return Remarks;
    }

    public void setRemarks(ArrayList<RemarkListItem> remarks) {
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

    public String getStatusDesc() {
        return StatusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        StatusDesc = statusDesc;
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
}
