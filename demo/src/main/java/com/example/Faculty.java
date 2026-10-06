package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Faculty record to store select faculty member information.
 * @param displayName the faculty member's display name
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Faculty(String displayName) {
}