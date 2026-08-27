package com.example;

import com.ahimsasystems.chenup.annotations.Entity;
import com.ahimsasystems.chenup.core.PersistenceCapable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

// © 2025 Stephen W. Strom
// Licensed under the MIT License. See LICENSE file in the project root for details.

@Entity
public interface Person extends PersistenceCapable
{
    // Should the IDs be exposed to the user?
    // UUID getId();
    // void setId(UUID id);

    String getName();
    void setName(String name);

    LocalDate getBirthDate();
    void setBirthDate(LocalDate birthDate);

    Instant getBirthInstant();
    void setBirthInstant(Instant birthInstant);

    PersonName getSeparateName();
    void setSeparateName(PersonName separatedName);

    default int age() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = getCurrentTime().atZone(zone).toLocalDate();
        int age = today.getYear() - getBirthDate().getYear();
        if (today.getDayOfYear() < getBirthDate().getDayOfYear()) {
            age--;
        }
        return age;
    }






}
