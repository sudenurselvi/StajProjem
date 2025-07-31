package com.denemestaj.stajproje1.controller;

import com.denemestaj.stajproje1.model.EnergyData;
import com.denemestaj.stajproje1.repository.EnergyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/energy")
@RequiredArgsConstructor
public class Controller {

    private final EnergyDataRepository repository;


    @GetMapping
    public ResponseEntity<List<EnergyData>> getAllEnergyData() {
        return ResponseEntity.ok(repository.findAll()); //get
    }


    @GetMapping("/seriNo/{seriNo}")
    public ResponseEntity<List<EnergyData>> getBySeriNo(@PathVariable String seriNo) {
        List<EnergyData> dataList = repository.findBySayacSeriNo(seriNo);
        if (dataList.isEmpty()) {
            return ResponseEntity.notFound().build();  //get
        }
        return ResponseEntity.ok(dataList);
    }


    @GetMapping("/updateBySeriNo/{seriNo}")
    public ResponseEntity<String> updateBySeriNo(@PathVariable String seriNo) {
        List<EnergyData> dataList = repository.findBySayacSeriNo(seriNo);

        if (dataList.isEmpty()) {
            return ResponseEntity.notFound().build(); //update
        }

        EnergyData data = dataList.get(0);
        data.setSayacMarkasi("B1009 GÜNCELLENDİ");
        data.setSayacModeli("GÜNCELLENDİ MODEL");
        repository.save(data);

        return ResponseEntity.ok("veri başarıyla güncellendi.");
    }

    @GetMapping("/deleteBySeriNo/{seriNo}")
    public ResponseEntity<String> deleteBySeriNo(@PathVariable String seriNo) {
        List<EnergyData> dataList = repository.findBySayacSeriNo(seriNo);

        if (dataList.isEmpty()) {
            return ResponseEntity.notFound().build(); //delete
        }

        repository.delete(dataList.get(0));
        return ResponseEntity.ok("veri başarıyla silindi.");
    }
}
