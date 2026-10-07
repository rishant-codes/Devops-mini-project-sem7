package com.aqmp.backend.dto;

import com.aqmp.backend.model.Reading;

import java.util.List;

public record StationDetail(
        Long id,
        String name,
        String location,
        List<Reading> history
) {
}
