package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Dell3 on 19-09-2017.
 */

public class SaveExpenseModel implements Serializable {
    private String FromButton;
    private String Source;
    private SaveExpenseItem expenseItem;

    public String getFromButton() {
        return FromButton;
    }

    public void setFromButton(String fromButton) {
        FromButton = fromButton;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public SaveExpenseItem getExpenseItem() {
        return expenseItem;
    }

    public void setExpenseItem(SaveExpenseItem expenseItem) {
        this.expenseItem = expenseItem;
    }
    static public SaveExpenseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, SaveExpenseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
