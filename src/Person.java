import java.time.LocalDateTime;
import java.util.ArrayList;

public class Person {

    private String name;
    private final String email; // Unique identifier
    private final Schedule schedule;


    public Person(String name, String email) {
        this.name = name;
        this.email = email;
        schedule = new Schedule();
    }

    public boolean isAvailableThen(LocalDateTime timeSlot) {
        for (Meeting m : schedule.getMeetings()) {
            if (m.getTimeslot().equals(timeSlot)) return false;
        }
        return true;
    }

    public void scheduleMeeting(Meeting meeting) throws Exception {
        // check availability
        for (Meeting m : schedule.getMeetings()) {
            if (m.getTimeslot().equals(meeting.getTimeslot())){
                throw new Exception("Meeting timeslot is unavailable for "+this+" at: "+meeting.getTimeslot());
            }
        }
        schedule.addMeeting(meeting);
    }

    public void showSchedule() {
        // Find upcoming meetings
        ArrayList<Meeting> upcoming = new ArrayList<>();
        for (Meeting meeting : schedule.getMeetings()) {
            if (meeting.getTimeslot().isAfter(LocalDateTime.now())) { // TODO: Get server time etc.
                upcoming.add(meeting);
            }
        }
        // Print info on upcoming meetings
        System.out.println("\n## Upcoming meetings for "+this+" ##");
        for (Meeting meeting : upcoming) {
            meeting.print();
        }
    }

    public ArrayList<Meeting> getSchedule() {
        ArrayList<Meeting> upcoming = new ArrayList<>();
        for (Meeting meeting : schedule.getMeetings()) {
            if (meeting.getTimeslot().isAfter(LocalDateTime.now())) { // TODO: Get server time etc.
                upcoming.add(meeting);
            }
        }
        return upcoming;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String toString() {
        return name+"|"+email;
    }


    /**
     * Each Person has their own Schedule
     */
    private class Schedule {
        private final ArrayList<Meeting> meetings;

        private Schedule() {
            meetings = new ArrayList<Meeting>();
        }

        private ArrayList<Meeting> getMeetings() {
            return meetings;
        }

        private void addMeeting(Meeting meeting) {
            meetings.add(meeting);
        }
    }

}
