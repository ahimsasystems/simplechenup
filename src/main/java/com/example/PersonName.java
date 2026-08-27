package com.example;

// © 2025 Stephen W. Strom
// Licensed under the MIT License. See LICENSE file in the project root for details.
public record PersonName(String givenName, String surName, boolean alias) {

    public PersonName(String givenName, String surName) {
        this(givenName, surName, false);
    }
}
