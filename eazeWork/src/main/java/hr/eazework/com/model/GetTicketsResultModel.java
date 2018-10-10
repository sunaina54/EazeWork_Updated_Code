package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 05-10-2018.
 */

public class GetTicketsResultModel extends GenericResponse implements Serializable {
    private ArrayList<TicketItem> Tickets;

    public ArrayList<TicketItem> getTickets() {
        return Tickets;
    }

    public void setTickets(ArrayList<TicketItem> tickets) {
        Tickets = tickets;
    }

    static public GetTicketsResultModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetTicketsResultModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
