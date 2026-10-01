package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Faculty record to store select faculty member information.
 * @param displayName the faculty member's display name
 * @param emailAddress the faculty member's email address
 * @param bannerId the faculty member's Banner ID
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Faculty(String displayName, String emailAddress, String bannerId) {
}