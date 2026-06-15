package com.wteam.backend.booking.dto;

import java.time.LocalDate;

public record UnavailableDateRange(LocalDate startDate, LocalDate endDate) {}
