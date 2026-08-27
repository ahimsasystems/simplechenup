package com.example;

// © 2025 Stephen W. Strom
// Licensed under the MIT License. See LICENSE file in the project root for details.

import com.ahimsasystems.chenup.annotations.Association;
import com.ahimsasystems.chenup.core.PersistenceCapable;

import java.time.LocalDate;

@Association
public interface Employment extends PersistenceCapable {

    Person getEmployee();

    void setEmployee(Person employee);

    Organization getEmployer();

    void setEmployer(Organization employer);

    LocalDate getStartDate();

    void setStartDate(LocalDate startDate);

    LocalDate getEndDate();

    void setEndDate(LocalDate endDate);

    void setStartDateTime(java.time.Instant startDateTime );

    java.time.Instant getStartDateTime();


}
