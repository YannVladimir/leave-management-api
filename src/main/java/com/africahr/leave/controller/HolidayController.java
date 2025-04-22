package com.africahr.leave.controller;

import com.africahr.leave.model.dto.HolidayDTO;
import com.africahr.leave.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday Management", description = "APIs for managing public holidays")
public class HolidayController {
    private final HolidayService holidayService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new holiday", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<HolidayDTO> createHoliday(@Valid @RequestBody HolidayDTO holidayDTO) {
        return ResponseEntity.ok(holidayService.createHoliday(holidayDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing holiday", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<HolidayDTO> updateHoliday(
            @PathVariable Long id,
            @Valid @RequestBody HolidayDTO holidayDTO) {
        return ResponseEntity.ok(holidayService.updateHoliday(id, holidayDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a holiday", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all holidays", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<HolidayDTO>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    @GetMapping("/public")
    @Operation(summary = "Get all public holidays")
    public ResponseEntity<List<HolidayDTO>> getPublicHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    @GetMapping("/between")
    @Operation(summary = "Get holidays between dates")
    public ResponseEntity<List<HolidayDTO>> getHolidaysBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(holidayService.getHolidaysBetweenDates(startDate, endDate));
    }
} 