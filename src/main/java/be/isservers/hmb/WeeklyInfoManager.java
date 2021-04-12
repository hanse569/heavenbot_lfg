package be.isservers.hmb;

import be.isservers.hmb.weeklyInfo.Affixes;
import be.isservers.hmb.weeklyInfo.WeeklyInfo;

import java.util.ArrayList;
import java.util.List;

class WeeklyInfoManager {
    private final List<WeeklyInfo> events = new ArrayList<>();

    WeeklyInfoManager() {
        addEvent(new Affixes());
    }

    private void addEvent(WeeklyInfo event){
        boolean nameFound = this.events.stream().anyMatch((it) -> it.getClass().getName().equals(event.getClass().getName()));

        if (nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        events.add(event);
    }

    void Load() {
        for (WeeklyInfo event : events) {
            WeeklyInfo.start(event);
        }
    }
}
