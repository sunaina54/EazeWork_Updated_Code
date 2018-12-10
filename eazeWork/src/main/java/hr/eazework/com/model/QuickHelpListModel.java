package hr.eazework.com.model;

import java.io.Serializable;

/**
 * Created by SUNAINA on 04-12-2018.
 */

public class QuickHelpListModel implements Serializable {
    private String Link;
    private String Text;
    private String SeqNo;

    public String getSeqNo() {
        return SeqNo;
    }

    public void setSeqNo(String seqNo) {
        SeqNo = seqNo;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }


}
