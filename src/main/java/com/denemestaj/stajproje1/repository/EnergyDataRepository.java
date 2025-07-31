package com.denemestaj.stajproje1.repository;

import com.denemestaj.stajproje1.model.EnergyData;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.time.LocalTime;




public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
    List<EnergyData> findBySayacSeriNoAndSayacSaati(String sayacSeriNo, LocalTime sayacSaati);

}


