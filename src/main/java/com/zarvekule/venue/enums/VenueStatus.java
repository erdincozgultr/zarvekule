package com.zarvekule.venue.enums;

public enum VenueStatus {
    PENDING_APPROVAL, // Kullanıcı oluşturdu, admin onayı bekliyor
    PUBLISHED,        // Yayında, herkes görebilir (Eski ACTIVE)
    CLAIM_PENDING,    // Bir kullanıcı sahiplik iddia etti, onay bekliyor
    CLOSED,           // Mekan kapandı
    REJECTED          // Onaylanmadı
}