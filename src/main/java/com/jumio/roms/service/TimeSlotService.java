package com.jumio.roms.service;

import com.jumio.roms.config.AppProperties;
import com.jumio.roms.domain.enums.MenuType;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class TimeSlotService {

    private final ZoneId zoneId;

    public TimeSlotService(AppProperties props) {
        this.zoneId = ZoneId.of(props.getTimezone());
    }

    public ZoneId zoneId() {
        return zoneId;
    }

    public MenuType menuTypeAt(Instant instant) {
        ZonedDateTime zdt = instant.atZone(zoneId);
        LocalTime t = zdt.toLocalTime();

        // Breakfast: 06:00–11:00, Lunch: 11:00–16:00, Dinner: 16:00–22:00
        if (!t.isBefore(LocalTime.of(6, 0)) && t.isBefore(LocalTime.of(11, 0))) return MenuType.BREAKFAST;
        if (!t.isBefore(LocalTime.of(11, 0)) && t.isBefore(LocalTime.of(16, 0))) return MenuType.LUNCH;
        if (!t.isBefore(LocalTime.of(16, 0)) && t.isBefore(LocalTime.of(22, 0))) return MenuType.DINNER;

        // Outside service hours => closest upcoming menu type is dinner? Business choice.
        // We'll treat it as DINNER for validation simplicity, but you'll likely reject such orders in real life.
        return MenuType.DINNER;
    }
}
