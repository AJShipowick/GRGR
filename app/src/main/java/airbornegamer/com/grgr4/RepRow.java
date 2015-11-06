package airbornegamer.com.grgr4;

import android.graphics.drawable.BitmapDrawable;

public class RepRow {
    public BitmapDrawable repPic;
    public String repInfo;
    public String repID;
    public BitmapDrawable repParty;

    public RepRow(BitmapDrawable repPic, String repInfo, String repID, BitmapDrawable repParty) {
        super();
        this.repPic = repPic;
        this.repInfo = repInfo;
        this.repID = repID;
        this.repParty = repParty;
    }
}
