package be.isservers.hmb.lfg;

import be.isservers.hmb.lfg.library.OrganizedDate;

public class EditEvent {

    private int action = -1;
    public int etape = 0;
    public int type = -1;
    private OrganizedDate od = null;

    public static int ADD = 1;
    public static int MODIFY = 2;
    public static int DELETE = 3;
    public static int LOCKED = 4;
    public static int UNLOCKED = 5;

    public EditEvent(int action, OrganizedDate od) {
        this.action = action;
        this.od = od;
    }

    public int getAction() {
        return action;
    }

    public OrganizedDate getOd() {
        return od;
    }

    public void setOd(OrganizedDate od) {
        this.od = od;
    }
}