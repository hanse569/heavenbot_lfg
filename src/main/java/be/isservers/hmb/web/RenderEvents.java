package be.isservers.hmb.web;

import be.isservers.hmb.lfg.library.OrganizedDate;

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
        template = template.replace("{{id}}",String.valueOf(this.od.getId()));
        template = template.replace("{{classState}}",this.classState);
        template = template.replace("{{instance}}",this.od.getInstance().getName());
        template = template.replace("{{difficulte}}",this.difficulte);
        template = template.replace("{{date}}",this.od.getDateToString());
        return template;
    }

    String template =
        "<tr class='hoverTr clickable-row' data-href='index.php?p=event&id={{id}}'>" +
            "<td class='text-bold-500'>" +
                "<div class='btn icon me-2 {{classState}}'>" +
                    "<i data-feather='check'></i>" +
                "</div>" +
                "{{instance}}</td>" +
            "<td>{{difficulte}}</td>" +
            "<td>{{date}}</td>" +
        "</tr>";
}
