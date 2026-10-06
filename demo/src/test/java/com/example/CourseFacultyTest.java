package com.example;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class CourseFacultyTest {

    @Test
    public void mapsFacultyDisplayNamesFromCourseJson() throws Exception {
        String json = """
                {
                  "id": "103267",
                  "subject": "CSC",
                  "courseNumber": "101",
                  "faculty": [
                    {
                      "displayName": "Bondok, Atef",
                      "emailAddress": "bondoka@easternct.edu",
                      "bannerId": "1292"
                    }
                  ]
                }
                """;

        Course course = new ObjectMapper().readValue(json, Course.class);

        assertEquals(1, course.faculty().size());
        assertEquals("Bondok, Atef", course.faculty().get(0).displayName());
    }
}
