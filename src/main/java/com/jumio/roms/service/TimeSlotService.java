package com.jumio.roms.service;

import com.jumio.roms.domain.enums.MenuType;

import java.time.Instant;
import java.time.ZoneId;

public interface TimeSlotService {
    ZoneId zoneId();
    MenuType menuTypeAt(Instant instant);
}
