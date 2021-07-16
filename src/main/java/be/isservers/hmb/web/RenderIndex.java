package be.isservers.hmb.web;

import be.isservers.hmb.lfg.library.OrganizedDate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RenderIndex implements Render{
    String instance;
    String classState;
    String difficulte;

    public RenderIndex(OrganizedDate od) {
        this.instance = od.getInstance().getName();

        if (od.isLocked()) {
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
        String template = new String(Files.readAllBytes(new File(getClass().getClassLoader().getResource("pages/item/indexItem.peb").getFile()).toPath()));

        template = template.replace("{{classState}}", this.classState);
        template = template.replace("{{instance}}", this.instance);
        template = template.replace("{{difficulte}}", this.difficulte);
        return template;
    }
}
