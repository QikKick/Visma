package com.example.internshipproject.Controller;

import com.example.internshipproject.Model.Meeting;
import com.example.internshipproject.Service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("/create")
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        Meeting createdMeeting = meetingService.createMeeting(meeting);
        return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable String id, @RequestParam String responsiblePerson) {
        meetingService.deleteMeeting(id, responsiblePerson);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/addPerson")
    public ResponseEntity<String> addPersonToMeeting(@PathVariable String id, @RequestParam String person, @RequestParam LocalDateTime time) {
        String result = meetingService.addPersonToMeeting(id, person, time);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}/removePerson")
    public ResponseEntity<Void> removePersonFromMeeting(@PathVariable String id, @RequestParam String person) {
        meetingService.removePersonFromMeeting(id, person);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> listMeetings(@RequestParam(required = false) String description,
                                                      @RequestParam(required = false) String responsiblePerson,
                                                      @RequestParam(required = false) Meeting.Category category,
                                                      @RequestParam(required = false) Meeting.Type type,
                                                      @RequestParam(required = false) LocalDateTime startDate,
                                                      @RequestParam(required = false) LocalDateTime endDate,
                                                      @RequestParam(required = false) Integer minAttendees) {
        List<Meeting> meetings = meetingService.listMeetings(description, responsiblePerson, category, type, startDate, endDate, minAttendees);
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }
}
