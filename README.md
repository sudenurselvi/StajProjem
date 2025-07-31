# Enerji Verisi Projesi

Bu projede, RabbitMQ’dan Base64 ile şifrelenmiş enerji verilerini alıp Java ile çözüyoruz (decode). Sonra mesajdaki OBIS kodlarına göre verileri ayırıp PostgreSQL’e kaydediyoruz.

## Nasıl Çalışıyor?

1. Producer Base64 kodlu mesajı RabbitMQ kuyruğuna gönderiyor.
2. Consumer kuyruğu dinliyor, mesajı alıyor ve Base64’ten UTF-8 olarak çözüyor.
3. Mesajdaki OBIS kodlarına göre (örneğin 1.8.0, 96.1.0 gibi) değerleri çekiyoruz.
4. Bu verileri enerji tablosuna kaydediyoruz.

## Enerji Tablosunda Neler Var?

- Sayaç Markası (96.1.0)
- Seri No (96.1.1)
- Toplam Pozitif Enerji (1.8.0)
- Toplam Negatif Enerji (2.8.0)
- Anlık Akım, Gerilim, Frekans vb.

## Kurulum ve Çalıştırma

- RabbitMQ ve PostgreSQL çalışır durumda olmalı.
- Veritabanı bağlantı ayarlarını `application.properties` dosyasından yap.
- Uygulamayı çalıştır.
- Producer’dan mesaj gönder, Consumer veriyi alıp işlesin.

## CRUD İşlemleri

Bu proje kapsamında **EnergyData** modeli üzerinde temel CRUD (Create, Read, Update, Delete) işlemleri REST API ile sağlanmaktadır.

- **Create:** `GET /api/energy/createMock` adresi üzerinden örnek veri oluşturabilirsiniz. (Test amaçlıdır, gerçek projelerde POST metodu kullanılması önerilir.)
- **Read:**
    - Tüm verileri listelemek için: `GET /api/energy`
    - Sayaç seri numarasına göre arama için: `GET /api/energy/seriNo/{seriNo}`
- **Update:** Belirli seri numarasına sahip veriyi güncellemek için: `GET /api/energy/updateBySeriNo/{seriNo}`
- **Delete:** Belirli seri numarasına sahip veriyi silmek için: `GET /api/energy/deleteBySeriNo/{seriNo}`

Test işlemleri tarayıcı üzerinden kolayca yapılabilir. Daha gelişmiş kullanım için API istemcileri (Postman vb.) kullanılabilir.


## Notlar

- OBIS kodları mesajda farklı sırada olabilir.
- Decode işlemi Base64 → UTF-8 ile yapılır.
- Daha fazla OBIS kodu için: https://onemeter.com/docs/device/obis/

---

**Hazırlayan:** Sude Nur Selvi  
2025 Staj Projesi


