package be.isservers.hmb.web;

import be.isservers.hmb.lfg.library.OrganizedDate;

import java.util.Calendar;

public class RenderEvents extends Render{
    OrganizedDate od;

    String nameState;
    String classState;
    String difficulte;

    public RenderEvents(OrganizedDate od, Boolean isArchived) {
        this.od = od;

        if (isArchived) {
            this.nameState = "Terminé";
            this.classState = "btn-secondary";
        } else if (od.isLocked()) {
            this.nameState = "Verrouillé";
            this.classState = "btn-warning";
        } else {
            this.nameState = "Ouvert";
            this.classState = "btn-success";
        }

        if (od.getDifficulty() == 0) {
            if (od.getInstance().getType() == 3) {
                this.difficulte = "Non coté";
            } else {
                this.difficulte = "Normal";
            }
        } else if (od.getDifficulty() == 1) {
            if (od.getInstance().getType() == 3) {
                this.difficulte = "Coté";
            } else {
                this.difficulte = "Heroique";
            }
        } else if (od.getDifficulty() == 2) {
            this.difficulte = "Mythique";
        } else {
            this.difficulte = "(Null)";
        }
    }

    public String build() {
        String buffer1,buffer2,buffer3;
        buffer1 = surround("i data-feather='check'");
        buffer1 = surround("div class='btn icon me-2 " + this.classState + "'", buffer1);
        buffer1 = surround("td class='text-bold-500'", buffer1 + this.od.getInstance());

        buffer2 = surround("td", this.difficulte);

        buffer3 = surround("td", od.getDateToString());

        return surround("tr class='hoverTr clickable-row' data-href='index.php?p=event&id=" + od.getId() + " '", buffer1 + buffer2 + buffer3);
    }
}
