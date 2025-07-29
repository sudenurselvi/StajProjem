package com.denemestaj.stajproje1.service;

import com.denemestaj.stajproje1.model.EnergyData;
import com.denemestaj.stajproje1.repository.EnergyDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // loglama için Lombok'un @Slf4j anotasyonu
public class EnergyDataService {

    private final EnergyDataRepository repository;

    public void parseAndSave(String decodedMessage, String hamVeri) {
        log.info("Veri çözümleme başlatıldı.");
        log.debug("Decoded mesaj: {}", decodedMessage);
        log.debug("Ham veri: {}", hamVeri);
        EnergyData data = new EnergyData();


        data.setHamVeri(hamVeri);


        data.setVeriGelisTarihi(parseGelisTarihi(decodedMessage));
        log.info("Veri geliş tarihi: {}", data.getVeriGelisTarihi());


        data.setSayacMarkasi(parseSayacMarkasiKodu(decodedMessage));
        data.setSayacModeli(parseSayacModelKodu(decodedMessage));
        data.setSayacSeriNo(parseSayacSeriNo(decodedMessage));

        log.info("Sayaç markası: {}", data.getSayacMarkasi());
        log.info("Sayaç modeli: {}", data.getSayacModeli());
        log.info("Sayaç seri no: {}", data.getSayacSeriNo());


        data.setSayacSaati(parseSaat(parseKod(decodedMessage, "0.9.1")));
        data.setSayacTarihi(parseTarih(parseKod(decodedMessage, "0.9.2")));

        log.info("Sayaç saati: {}", data.getSayacSaati());
        log.info("Sayaç tarihi: {}", data.getSayacTarihi());


        data.setToplamPozitifEnerji(parseDecimalValue(parseKod(decodedMessage, "1.8.0")));
        data.setT1(parseDecimalValue(parseKod(decodedMessage, "1.8.1"))); // Eğer mesajda 1.8.0*1, 1.8.1*1 vb. yoksa direkt 1.8.1 kullanılır
        data.setT2(parseDecimalValue(parseKod(decodedMessage, "1.8.2")));
        data.setT3(parseDecimalValue(parseKod(decodedMessage, "1.8.3")));

        log.info("Toplam pozitif enerji: {}", data.getToplamPozitifEnerji());
        log.info("T1: {}", data.getT1());
        log.info("T2: {}", data.getT2());
        log.info("T3: {}", data.getT3());


        data.setEnerjiBirimi("KWH");
        data.setT1Birimi("KWH");
        data.setT2Birimi("KWH");
        data.setT3Birimi("KWH");


        data.setYazKisAktif(parseYazKisAktif(decodedMessage));
        log.info("Yaz/Kış aktif: {}", data.getYazKisAktif());

        log.info("Veritabanına kayıt ediliyor...");
        repository.save(data);
        log.info("Veri başarıyla kaydedildi. ID: {}", data.getId()); // Kaydedilen ID'yi logla
    }


    private String parseKod(String text, String kod) {
        if (text == null || text.isEmpty() || kod == null || kod.isEmpty()) {
            log.debug("parseKod: Girdi metni veya kod boş/null. Kod: {}", kod);
            return null;
        }
        String escapedKod = Pattern.quote(kod);

        Pattern pattern = Pattern.compile(escapedKod + "\\s*\\(([^)]+?)\\)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            log.debug("parseKod: Eşleşme bulundu. Kod: '{}', Değer: '{}'", kod, matcher.group(1));
            return matcher.group(1);
        } else {
            log.debug("parseKod: Eşleşme bulunamadı. Kod: '{}', Regex: '{}'", kod, pattern.pattern());
            return null;
        }
    }


    private BigDecimal parseDecimalValue(String valueWithUnit) {
        if (valueWithUnit == null || valueWithUnit.isEmpty()) {
            log.debug("parseDecimalValue: Girdi değeri boş/null.");
            return null;
        }

        Pattern pattern = Pattern.compile("([0-9.]+)(?:\\*[A-Z]+)?");
        Matcher matcher = pattern.matcher(valueWithUnit);

        if (matcher.find()) {
            String val = matcher.group(1); // Sayısal kısım
            try {
                return new BigDecimal(val);
            } catch (NumberFormatException e) {
                log.warn("parseDecimalValue: Sayı formatı hatalı: '{}'. Hata: {}", val, e.getMessage());
                return null;
            }
        } else {
            log.warn("parseDecimalValue: Sayısal değer bulunamadı: '{}'", valueWithUnit);
            return null;
        }
    }


    private LocalDateTime parseGelisTarihi(String text) {

        Pattern pattern = Pattern.compile(".*?(\\d{2}-\\d{2}-\\d{2});(\\d{2}:\\d{2}:\\d{2})\\d{2}.*?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String tarihPart = matcher.group(1);
            String saatPart = matcher.group(2);

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(tarihPart + " " + saatPart, formatter);
            } catch (Exception e) {
                log.warn("parseGelisTarihi: Tarih/saat parse edilemedi: '{};{}''. Hata: {}", tarihPart, saatPart, e.getMessage());
            }
        } else {
            log.warn("parseGelisTarihi: Veri geliş tarihi/saati deseni bulunamadı.");
        }
        return null;
    }


    private String parseSayacMarkasiKodu(String text) {
        // C(sayı).(MARKA).(sayı) -> Marka kodunu yakala (örneğin KMY)
        Pattern pattern = Pattern.compile("C\\d+\\.([A-Z]+)\\.\\d+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            log.debug("parseSayacMarkasiKodu: Sayaç markası bulundu: {}", matcher.group(1));
            return matcher.group(1);
        } else {
            log.warn("parseSayacMarkasiKodu: Sayaç markası kodu bulunamadı.");
        }
        return null;
    }


    private String parseSayacModelKodu(String text) {

        Pattern pattern = Pattern.compile("C(\\d+)\\.[A-Z]+\\.\\d+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            log.debug("parseSayacModelKodu: Sayaç modeli bulundu: {}", matcher.group(1));
            return matcher.group(1);
        } else {
            log.warn("parseSayacModelKodu: Sayaç modeli kodu bulunamadı.");
        }
        return null;
    }


    private String parseSayacSeriNo(String text) {
        // C(sayı).(harf).(SERINO) -> Seri numarasını yakala (örneğin 2556)
        Pattern pattern = Pattern.compile("C\\d+\\.[A-Z]+\\.(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            log.debug("parseSayacSeriNo: Sayaç seri no bulundu: {}", matcher.group(1));
            return matcher.group(1);
        } else {
            log.warn("parseSayacSeriNo: Sayaç seri numarası bulunamadı.");
        }
        return null;
    }


    private LocalDate parseTarih(String str) {
        if (str == null || str.isEmpty()) {
            log.debug("parseTarih: Girdi stringi boş/null.");
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
            return LocalDate.parse(str, formatter);
        } catch (Exception e) {
            log.warn("parseTarih: Sayaç tarihi parse edilemedi: '{}'. Hata: {}", str, e.getMessage());
            return null;
        }
    }

    private LocalTime parseSaat(String str) {
        if (str == null || str.isEmpty()) {
            log.debug("parseSaat: Girdi stringi boş/null.");
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return LocalTime.parse(str, formatter);
        } catch (Exception e) {
            log.warn("parseSaat: Sayaç saati parse edilemedi: '{}'. Hata: {}", str, e.getMessage());
            return null;
        }
    }


    private Boolean parseYazKisAktif(String text) {
        String yazKisStr = parseKod(text, "96.93"); // 96.93(0) gibi bir ifade arar
        if (yazKisStr != null) {
            String trimmedStr = yazKisStr.trim();
            if ("1".equals(trimmedStr)) {
                log.debug("parseYazKisAktif: Yaz/Kış aktif: true");
                return true;
            } else if ("0".equals(trimmedStr)) {
                log.debug("parseYazKisAktif: Yaz/Kış aktif: false");
                return false;
            }
        }
        log.warn("parseYazKisAktif: Yaz/Kış aktif değeri bulunamadı veya tanınmayan format: '{}'", yazKisStr);
        return null;
    }
}
