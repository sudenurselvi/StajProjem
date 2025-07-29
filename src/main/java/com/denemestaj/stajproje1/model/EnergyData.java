package com.denemestaj.stajproje1.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "enerji_tablom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime veriGelisTarihi;

    private String sayacMarkasi;
    private String sayacModeli;
    private String sayacSeriNo;

    private LocalTime sayacSaati;
    private LocalDate sayacTarihi;

    private BigDecimal toplamPozitifEnerji;
    private BigDecimal t1;
    private BigDecimal t2;
    private BigDecimal t3;

    private String enerjiBirimi;
    private String t1Birimi;
    private String t2Birimi;
    private String t3Birimi;

    private Boolean yazKisAktif;

    @Column(name = "ham_veri", columnDefinition = "TEXT")
    private String hamVeri;

}
