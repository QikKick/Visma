package com.example.internshipproject.Service;

import com.example.internshipproject.Model.Meeting;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeetingService {
    private static final String MEETINGS_FILE = "meetings.json";
    private final ObjectMapper objectMapper;
    private List<Meeting> meetings;

    public MeetingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.meetings = new ArrayList<Meeting>();
        loadMeetingsFromFile();
    }

    public Meeting createMeeting(Meeting meeting) {
        meeting.setId(UUID.randomUUID().toString());
        meetings.add(meeting);
        saveMeetingsToFile();
        return meeting;
    }

    public void deleteMeeting(String id, String responsiblePerson) {
        Meeting meeting = findMeetingById(id);
        if (meeting != null && meeting.getResponsiblePerson().equals(responsiblePerson)) {
            meetings.remove(meeting);
            saveMeetingsToFile();
        }
    }

    public String addPersonToMeeting(String id, String person, LocalDateTime time) {
        Meeting meeting = findMeetingById(id);
        if (meeting != null) {
            if (meeting.getAttendees().contains(person)) {
                return "Person is already in the meeting.";
            }

            if (isPersonAvailable(person, time)) {
                meeting.getAttendees().add(person);
                saveMeetingsToFile();
                return "Person added to the meeting.";
            } else {
                return "Warning: Person is already in another meeting at the same time.";
            }
        }
        return "Meeting not found.";
    }

    public void removePersonFromMeeting(String id, String person) {
        Meeting meeting = findMeetingById(id);
        if (meeting != null && !meeting.getResponsiblePerson().equals(person)) {
            meeting.getAttendees().remove(person);
            saveMeetingsToFile();
        }
    }

    public List<Meeting> listMeetings(String description, String responsiblePerson, Meeting.Category category, Meeting.Type type, LocalDateTime startDate, LocalDateTime endDate, Integer minAttendees) {
        return meetings.stream()
                .filter(meeting -> description == null || meeting.getDescription().toLowerCase().contains(description.toLowerCase()))
                .filter(meeting -> responsiblePerson == null || meeting.getResponsiblePerson().equals(responsiblePerson))
                .filter(meeting -> category == null || meeting.getCategory() == category)
                .filter(meeting -> type == null || meeting.getType() == type)
                .filter(meeting -> startDate == null || (meeting.getStartDate().isEqual(startDate) || meeting.getStartDate().isAfter(startDate)))
                .filter(meeting -> endDate == null || (meeting.getEndDate().isEqual(endDate) || meeting.getEndDate().isBefore(endDate)))
                .filter(meeting -> minAttendees == null || meeting.getAttendees().size() >= minAttendees)
                .collect(Collectors.toList());
    }

    private Meeting findMeetingById(String id) {
        return meetings.stream().filter(meeting -> meeting.getId().equals(id)).findFirst().orElse(null);
    }

    private boolean isPersonAvailable(String person, LocalDateTime time) {
        return meetings.stream().noneMatch(meeting -> meeting.getAttendees().contains(person) && (time.isAfter(meeting.getStartDate()) && time.isBefore(meeting.getEndDate())));
    }

    private void loadMeetingsFromFile() {
        File file = new File(MEETINGS_FILE);
        if (file.exists()) {
            try {
                meetings = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Meeting.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveMeetingsToFile() {
        try {
            objectMapper.writeValue(new File(MEETINGS_FILE), meetings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
