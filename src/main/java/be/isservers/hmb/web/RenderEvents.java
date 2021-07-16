package be.isservers.hmb.web;

import be.isservers.hmb.lfg.library.OrganizedDate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RenderEvents implements Render {
    String id;
    String instance;
    String date;
    String classState;
    String difficulte;

    public RenderEvents(OrganizedDate od, Boolean isArchived) {
        this.id = String.valueOf(od.getId());
        this.instance = od.getInstance().getName();
        this.date = od.getDateToString();

        if (isArchived) {
            this.classState = "btn-secondary";
        } else if (od.isLocked()) {
            this.classState = "btn-warning";
        } else {
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

    public String build() throws IOException {
        String template = new String(Files.readAllBytes(new File(getClass().getClassLoader().getResource("pages/item/archiveItem.peb").getFile()).toPath()));

        template = template.replace("{{id}}", this.id);
        template = template.replace("{{classState}}", this.classState);
        template = template.replace("{{instance}}", this.instance);
        template = template.replace("{{difficulte}}", this.difficulte);
        template = template.replace("{{date}}", this.date);
        return template;
    }
}
