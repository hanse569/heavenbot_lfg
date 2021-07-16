package be.isservers.hmb.lfg;

import be.isservers.hmb.lfg.library.OrganizedDate;

class EditEvent {

    private final int action;
    int etape = 0;
    int type = -1;
    private OrganizedDate od;

    static int ADD = 1;
    static int MODIFY = 2;
    static int DELETE = 3;
    static int LOCKED = 4;
    static int UNLOCKED = 5;

    EditEvent(int action, OrganizedDate od) {
        this.action = action;
        this.od = od;
    }

    int getAction() {
        return action;
    }

    OrganizedDate getOd() {
        return od;
    }

    void setOd(OrganizedDate od) {
        this.od = od;
    }
}
