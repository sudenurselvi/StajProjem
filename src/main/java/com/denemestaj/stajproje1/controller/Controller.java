package com.denemestaj.stajproje1.controller;

import com.denemestaj.stajproje1.model.EnergyData;
import com.denemestaj.stajproje1.repository.EnergyDataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/energy")
@RequiredArgsConstructor
public class Controller {

    private final EnergyDataRepository repository;

    @Operation(summary = "yeni enerji verisi ekle")
    @PostMapping
    public ResponseEntity<EnergyData> createEnergyData(@RequestBody EnergyData energyData) { //requestbody bodyden veri alıyor
        EnergyData saved = repository.save(energyData);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "seri no ve sayaç saatine göre veri getir")
    @GetMapping("/getBySeriNoAndTime")
    public ResponseEntity<EnergyData> getBySeriNoAndTime(
            @RequestParam String seriNo, //requestparam urlden veri alıyor
            @RequestParam
            @Parameter(description = "saat bilgisi", example = "11:06:25") // parameter gelen verinin ne olduğunu
            // tanımlar requestparamla kullanılır
            @Schema(type = "string", format = "time") //schema verinin içinde hangi alanlar olduğunu,
            // bunların tipini ve gerekli olup olmadığını tanımlar.
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime sayacSaati) { //Spring Boot’un gelen zaman (saat) parametresini doğru
        // formatta anlayabilmesi için kullanılır.



        List<EnergyData> dataList = repository.findBySayacSeriNoAndSayacSaati(seriNo, sayacSaati);

        if (dataList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dataList.get(0));
    }

    @Operation(summary = "seri no ve sayaç saatine göre veriyi güncelle")
    @PutMapping("/updateBySeriNoAndTime")
    public ResponseEntity<String> updateBySeriNoAndTime(
            @RequestParam String seriNo,
            @RequestParam
            @Parameter(description = "saat bilgisi", example = "11:06:25")
            @Schema(type = "string", format = "time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime sayacSaati) {

        List<EnergyData> dataList = repository.findBySayacSeriNoAndSayacSaati(seriNo, sayacSaati);

        if (dataList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EnergyData data = dataList.get(0);
        data.setSayacMarkasi("güncellendi");
        data.setSayacModeli("yeni model");
        repository.save(data);

        return ResponseEntity.ok("veri başarıyla güncellendi.");
    }

    @Operation(summary = "seri no ve sayaç saatine göre veriyi sil")
    @DeleteMapping("/deleteBySeriNoAndTime")
    public ResponseEntity<String> deleteBySeriNoAndTime(
            @RequestParam String seriNo,
            @RequestParam
            @Parameter(description = "saat bilgisi", example = "11:06:25")
            @Schema(type = "string", format = "time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime sayacSaati) {

        List<EnergyData> dataList = repository.findBySayacSeriNoAndSayacSaati(seriNo, sayacSaati);

        if (dataList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        repository.delete(dataList.get(0));
        return ResponseEntity.ok("veri başarıyla silindi.");
    }
}
