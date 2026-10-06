package com.example;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    @Test
    public void mapsCreditHourSessionFromMeetingTimeJson() throws Exception {
        String json = """
                {
                  "meetingsFaculty": [
                    {
                      "meetingTime": {
                        "beginTime": "1100",
                        "endTime": "1150",
                        "creditHourSession": 3.0
                      }
                    }
                  ]
                }
                """;

        Course course = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(json, Course.class);

        assertEquals(3.0, course.meetingsFaculty().get(0).meetingTime().creditHourSession(), 0.0);
    }
}
