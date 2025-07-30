package com.denemestaj.stajproje1.controller;

import com.denemestaj.stajproje1.model.EnergyData;
import com.denemestaj.stajproje1.repository.EnergyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/energy")
@RequiredArgsConstructor
public class Controller {

    private final EnergyDataRepository repository;


    @PostMapping
    public ResponseEntity<EnergyData> createEnergyData(@RequestBody EnergyData data) {
        EnergyData saved = repository.save(data);
        return ResponseEntity.ok(saved); //create enerji verisi ekleme
    }


    @GetMapping
    public ResponseEntity<List<EnergyData>> getAllEnergyData() {
        return ResponseEntity.ok(repository.findAll());  //read tum kayıtları getir
    }


    @GetMapping("/{id}")
    public ResponseEntity<EnergyData> getEnergyDataById(@PathVariable Long id) {
        Optional<EnergyData> data = repository.findById(id);
        return data.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); // read id ile kayıt getir
    }


    @PutMapping("/{id}")
    public ResponseEntity<EnergyData> updateEnergyData(@PathVariable Long id, @RequestBody EnergyData updatedData) {
        return repository.findById(id)
                .map(existing -> {
                    updatedData.setId(id);
                    return ResponseEntity.ok(repository.save(updatedData));
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); //  update id ile veriyi güncelle
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnergyData(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build(); // delete id ile veriyi sil
        }
        return ResponseEntity.notFound().build();
    }
}
