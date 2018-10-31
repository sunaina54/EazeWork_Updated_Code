package hr.eazework.com.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuItemModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean isAccess;
    private String isTicketAccess;

    private String mObjectId;
    private String mObjectDesc;
    private String mObjectDisplay;
    private String mSeqNo;
    private String mTypeID;
    private ArrayList<MenuItemModel> itemList;

    public static final String ATTANDANCE_KEY = "M0001";
    public static final String LEAVE_KEY = "M0002";
    public static final String PAY_SLIP_KEY = "M0003";
    public static final String LEAVE_REQUEST_KEY = "M0004";
    public static final String APPROVAL_KEY = "M0005";
    public static final String ATTENDANCE_MARKING = "M0006";
    public static final String TOUR_REQUEST = "M0007";
    public static final String WORK_FROM_HOME = "M0008";
    public static final String OD_REQUEST = "M0009";
    public static final String TEAM_KEY = "M0015";
    public static final String LOCATION_KEY = "M0012";
    public static final String EMPLOYEE_APPROVAL_KEY = "M0011";
    public static final String CREATE_LOCATION = "M0013";
    public static final String CREATE_EMPLOYEE = "M0010";
    public static final String CREATE_LEAVE = "M0004";
    public static final String VIEW_PROFILE_KEY = "M0016";
    public static final String EDIT_PROFILE_KEY = "M0017";
    public static final String CREATE_ADVANCE_KEY="M0018";
    public static final String CREATE_EXPENSE_KEY="M0019";
    public static final String ADVANCE_KEY="M0020";
    public static final String EXPENSE_KEY="M0021";
    public static final String TICKET_KEY="M0024";
    //public static final String OTHER_TICKET_KEY="M0025";

    public MenuItemModel(JSONObject jsonObject) {
        isAccess = jsonObject.optString("AccessYN", "N").equalsIgnoreCase("y");


        mObjectId = jsonObject.optString("ObjecID", "");
        if(mObjectId!=null && mObjectId.equalsIgnoreCase(TICKET_KEY)) {
            isTicketAccess = jsonObject.optString("AccessYN", "N");
        }
        mObjectDesc = jsonObject.optString("ObjectDesc", "");
        mObjectDisplay = jsonObject.optString("ObjectDisplay", "");
        mSeqNo = jsonObject.optString("SeqNo", "");
        mTypeID = jsonObject.optString("TypeID", "");



    }

    public MenuItemModel(JSONArray jsonArray) {
        itemList = new ArrayList<MenuItemModel>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                MenuItemModel itemModel;
                try {
                    itemModel = new MenuItemModel(jsonArray.getJSONObject(i));
                    itemList.add(itemModel);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }


    public String getmObjectDisplay() {
        return mObjectDisplay;
    }

    public void setmObjectDisplay(String mObjectDisplay) {
        this.mObjectDisplay = mObjectDisplay;
    }

    public String getmSeqNo() {
        return mSeqNo;
    }

    public void setmSeqNo(String mSeqNo) {
        this.mSeqNo = mSeqNo;
    }

    public String getmTypeID() {
        return mTypeID;
    }

    public void setmTypeID(String mTypeID) {
        this.mTypeID = mTypeID;
    }

    public boolean isAccess() {
        return isAccess;
    }

    public String getIsTicketAccess() {
        return isTicketAccess;
    }

    public void setIsTicketAccess(String isTicketAccess) {
        this.isTicketAccess = isTicketAccess;
    }

    public void setAccess(boolean isAccess) {
        this.isAccess = isAccess;
    }

    public String getmObjectId() {
        return mObjectId;
    }

    public void setmObjectId(String mObjectId) {
        this.mObjectId = mObjectId;
    }

    public String getmObjectDesc() {
        return mObjectDesc;
    }

    public void setmObjectDesc(String mObjectDesc) {
        this.mObjectDesc = mObjectDesc;
    }

    public ArrayList<MenuItemModel> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<MenuItemModel> itemList) {
        this.itemList = itemList;
    }

    public MenuItemModel getItemModel(String objectId) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        for (MenuItemModel model : itemList) {
            if (objectId.equalsIgnoreCase(model.getmObjectId())) {
                return model;
            }
        }
        return null;
    }

    public MenuItemModel getItemModelByDesc(String objectDesc) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        for (MenuItemModel model : itemList) {
            if (objectDesc.equalsIgnoreCase(model.getmObjectDesc())) {
                return model;
            }
        }
        return null;
    }
    public MenuItemModel getItemModelByDisplayObject(String objectDisplay) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        for (MenuItemModel model : itemList) {
            if (objectDisplay.equalsIgnoreCase(model.getmObjectDisplay())) {
                return model;
            }
        }
        return null;
    }

    public MenuItemModel getItemModelByTypeId(String getItemModelByTypeId) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        for (MenuItemModel model : itemList) {
            if (getItemModelByTypeId.equalsIgnoreCase(model.getmTypeID())) {
                return model;
            }
        }
        return null;
    }
}
