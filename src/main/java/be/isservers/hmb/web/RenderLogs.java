package be.isservers.hmb.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RenderLogs extends Render {
    private final String id;
    private final String title;
    private final String author;
    private final long date;

    public RenderLogs(String id, String title, String author, String date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = Long.parseLong(date);
    }

    public String render() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String buffer1;
        buffer1 = this.surround("h6", this.title);
        buffer1 += this.surround("p class='text-muted'", "publie par " + this.upperCaseFirst(this.author) + " le " + sdf.format(cal.getTime()));
        buffer1 = this.surround("div class='col-lg-9 col-md-9'", buffer1);

        String buffer2;
        buffer2 = this.surround("a href='https://fr.warcraftlogs.com/reports/" + this.id + "' target='_blank' class='btn btn-primary d-flex justify-content-center'", "Voir sur WarcraftLogs");
        buffer2 = this.surround("div class='col-lg-3 col-md-3' style='margin-bottom: 5px;'", buffer2);

        return this.surround("div class='row item-log'", buffer1 + buffer2);
    }
}