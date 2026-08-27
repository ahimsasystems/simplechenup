package com.example;

// © 2025 Stephen W. Strom
// Licensed under the MIT License. See LICENSE file in the project root for details.

import com.ahimsasystems.chenup.annotations.Entity;
import com.ahimsasystems.chenup.core.PersistenceCapable;

@Entity
public interface Organization extends PersistenceCapable {
    String getName();
    void setName(String name);
}
