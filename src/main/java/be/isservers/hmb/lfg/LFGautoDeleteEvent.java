package be.isservers.hmb.lfg;

<<<<<<< HEAD
import be.isservers.hmb.lfg.library.OrganizedDate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

public class LFGautoDeleteEvent implements ActionListener {

    public LFGautoDeleteEvent() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -14);
        Date dateBefore14Days = cal.getTime();

        for (OrganizedDate od : LFGdataManagement.listDate) {
            if (od.getDateToDate().before(dateBefore14Days)) {
                od.Delete();
            }
        }
    }
=======
public class LFGautoDeleteEvent {
>>>>>>> 9944e4cf72baaccc0e413ea889eff6a6246717f7
}
