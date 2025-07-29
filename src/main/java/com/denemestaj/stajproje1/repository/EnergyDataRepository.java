package com.denemestaj.stajproje1.repository;

import com.denemestaj.stajproje1.model.EnergyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {

}
