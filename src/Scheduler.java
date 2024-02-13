import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Scheduler {

    public enum MeetingDays {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    };

    private final List<MeetingDays> defaultPreferredDays;
    private final List<Integer> defaultPreferredHours; // preferred timeslots could be migrated to each Person
    // in a more advanced version
    private final ArrayList<Person> people;
    private final ArrayList<Meeting> meetings;
    private LocalDateTime currentTime;

    /**
     * Instantiates a new Scheduler containing default values for suggesting meetings for preferred timeslots.
     * Holds ArrayLists for people and meetings, storage is non-persistent.
     */
    public Scheduler() {
        people = new ArrayList<>();
        meetings = new ArrayList<>();
        defaultPreferredDays = new ArrayList<>() {{
            add(MeetingDays.MONDAY); add(MeetingDays.TUESDAY); add(MeetingDays.WEDNESDAY);
            add(MeetingDays.THURSDAY); add(MeetingDays.FRIDAY);
        }};
        defaultPreferredHours = new ArrayList<>() {{
            add(9); add(10); add(13); add(14);
        }};
        currentTime = LocalDateTime.now(); // TODO: Get server time etc.
    }

    /**
     * 1) Create persons with a name and unique email.
     * @param name  String for name of Person instance
     * @param email String for valid email address and unique identifier for Person instance
     * @throws Exception on invalid contact info or in case of duplicate unique email
     */
    public void createPerson(String name, String email) throws Exception {
        if (name == null || email == null) {
            throw new InvalidParameterException("String name and String email must be not-null");
        }

        if (name.isEmpty() || !isValidEmail(email)) {
            throw new InvalidParameterException("Person error: Invalid contact information.");
        }
        for (Person person : people) {
            if (person.getEmail().equals(email)) {
                throw new Exception("Person error: Unique Email already in use.");
            }
        }
        people.add(new Person(name, email));
        System.out.println("> Successfully added new person: " + name + " " + email);
    }

    /**
     * 2) Create meetings involving one or more persons at a given time slot
     * Method assumes the caller wants to create a new meeting for a given list of emails that have already
     * been added by createPerson(name, email)
     * @param stringTimeSlot String with date pattern "HH-dd-MM-yyyy", using 24-hour count.
     *                       E.g. "14-01-01-2024" returns 2024-01-01T14:00
     *                       Intentionally disregards minutes, seconds, etc. for the sake of whole hour timeslots.
     * @param emailParticipants List of Strings of emails of participants
     * @throws Exception if called with empty list of emailParticipants, or if list contains email not already added.
     */
    public void createMeeting(String stringTimeSlot, List<String> emailParticipants) throws Exception {
        if (stringTimeSlot == null || emailParticipants == null) {
            throw new InvalidParameterException("String timeSlot and List<String> emailParticipants must be not-null");
        }
        LocalDateTime timeSlot = formatStringToDatetime(stringTimeSlot);
        if (emailParticipants.isEmpty()) {
            throw new InvalidParameterException("Meeting must have at least 1 participant.");
        }
        ArrayList<Person> participants = new ArrayList<>();
        for (String email : emailParticipants) {
            participants.add(getPersonFromEmail(email));
        }
        // Check participant availability
        boolean allAvailable = false;
        for (Person person : participants) {
            allAvailable = person.isAvailableThen(timeSlot);
            if (!allAvailable) {
                System.out.println("\n¤ attempted createMeeting at: "+timeSlot
                        +" - Some participants not available at timeslot");
                return;
            }
        }
        // Schedule meeting
        Meeting meeting = new Meeting(timeSlot, participants);
        for (Person person : participants) {
            person.scheduleMeeting(meeting);
        }
        meetings.add(meeting);
        System.out.println("\n> Successfully created new meeting.");
        meeting.print();
    }

    /**
     * 4) Show the schedule, i.e., the upcoming meetings, for a given person.
     * Attempts to find existing Person from given email String.
     * @param email String of email of Person to show schedule for.
     */
    public void showScheduleForPerson(String email) {
        if (email == null) {
            throw new InvalidParameterException("String email must be not-null");
        }
        try {
            getPersonFromEmail(email).showSchedule();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 4) Show the schedule, i.e., the upcoming meetings, for a given person.
     * Assumes given Person already exists.
     * @param person Person instance to show (print) schedule for.
     */
    public void showScheduleForPerson(Person person) {
        if (person == null) {
            throw new InvalidParameterException("Person must be not-null");
        }
        person.showSchedule();
    }

    /**
     * Access schedule of person given by their email String
     * @param email String
     * @return ArrayList<Meeting> schedule of Person of given email String
     */
    public ArrayList<Meeting> getScheduleForPerson(String email) {
        if (email == null) {
            throw new InvalidParameterException("String email must be not-null");
        }
        try {
            return getPersonFromEmail(email).getSchedule();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Access schedule of given Person instance
     * @param person instance of type Person
     * @return ArrayList<Meeting> schedule of given Person instance
     */
    public ArrayList<Meeting> getScheduleForPerson(Person person) {
        if (person == null) {
            throw new InvalidParameterException("Person must be not-null");
        }
        return person.getSchedule();
    }

    /**
     * 5) Suggest one or more available timeslots for meetings given a group of persons.
     * Suggests timeslots given a list of emails of wanted participants.
     * Initialises suggestion on day after 'currentTime' at first given hour-element in list of preferred hours.
     * Searches forward in time starting from initial candidate timeslot, constrained by
     * 'preferredDays' and 'preferredHours'.
     * Checks each participant's schedule to find timeslots where all are available.
     * Will loop until given count of suggested timeslots has been reached.
     *
     * @param emailParticipants List of Strings of emails of participants for suggested meeting timeslot
     * @param count             int amount of number of timeslots suggested
     * @param preferredDays     List<MeetingDays> of days constraining timeslot suggestions
     * @param preferredHours    List<Integer> of hours constraining timeslot suggestions (1-24)
     * @return                  ArrayList<LocalDateTime> output result of type LocalDateTime
     */
    public ArrayList<LocalDateTime> suggestTimeslots(List<String> emailParticipants, int count,
                                                     List<MeetingDays> preferredDays, List<Integer> preferredHours) {
        if (emailParticipants == null || emailParticipants.isEmpty()) {
            throw new InvalidParameterException("emailParticipants must be be not-null and not-empty");
        }
        if (count < 1) {
            throw new InvalidParameterException("count must be > 0");
        }
        if (preferredDays == null || preferredDays.isEmpty() || preferredHours == null || preferredHours.isEmpty()) {
            throw new InvalidParameterException("preferredDays and preferredHours must be not-null and not-empty");
        }
        // Get ArrayList of Persons from given emails
        ArrayList<Person> participants = new ArrayList<>();
        try {
            for (String email : emailParticipants) participants.add(getPersonFromEmail(email));
        } catch (Exception e) {
            System.out.println(e);
        }
        // Init lists of preferred timeslots
        List<String> days = new ArrayList<>() {{
            for (MeetingDays day : preferredDays) {
                add(day.name());
            }
        }};
        List<Integer> hours = preferredHours;
        System.out.println("\nPreferred days: " + days + "\nPreferred hours: " + hours);
        // Init candidate timeslot to first preferred hour of next day
        LocalDateTime candidateLDT = currentTime;
        candidateLDT = candidateLDT.plusDays(1).withHour(hours.getFirst())
                .withMinute(0).withSecond(0).withNano(0);
        System.out.println("    checking forward starting at: "+candidateLDT+" "+candidateLDT.getDayOfWeek());
        // Begin loop to find viable timeslots
        ArrayList<LocalDateTime> timeslots = new ArrayList<>(); // output list
        ArrayList<LocalDateTime> occupied = new ArrayList<>();
        while (timeslots.size() < count) {
            while (!days.contains(candidateLDT.getDayOfWeek().name())) {
                candidateLDT = candidateLDT.plusDays(1);    // Increment to next preferred day
            }
            occupied.clear();
            for (Person person : participants) {
                if (!person.isAvailableThen(candidateLDT)) {
                    occupied.add(candidateLDT);
                    //System.out.println(person +" is occupied on: "+candidateLDT+" "+candidateLDT.getDayOfWeek());
                }
            }
            if (occupied.isEmpty() && !timeslots.contains(candidateLDT)) {
                timeslots.add(candidateLDT);
                System.out.println("    "+candidateLDT + " " + candidateLDT.getDayOfWeek()
                        + " is available for all participants!  (Timeslots found: "+timeslots.size()+" / "+count+")");
            } else {
                do {
                    candidateLDT = candidateLDT.plusHours(1);   // Increment to next preferred hour
                } while (!hours.contains(candidateLDT.getHour()));
            }
        }
        System.out.println("> Suggesting Timeslots for Meeting with Participants: "+emailParticipants+"\n "+timeslots);
        return timeslots;
    }

    /**
     * 5) Suggest one or more available timeslots for meetings given a group of persons.
     * Limited method signature of
     * suggestTimeslots(List<String> emailParticipants, int count,
     *                  List<MeetingDays> preferredDays, List<Integer> preferredHours)
     * - Defaults to count = 1 of returned suggested timeslots.
     * - Invokes with defaultPreferredDays, defaultPreferredHours
     * @param emailParticipants list of strings of emails of participants for suggested meeting timeslot
     * @return                  ArrayList<LocalDateTime> output result of type LocalDateTime
     */
    public ArrayList<LocalDateTime> suggestTimeslots(List<String> emailParticipants) {
        return suggestTimeslots(emailParticipants, 1, defaultPreferredDays, defaultPreferredHours);
    }

    /**
     * 5) Suggest one or more available timeslots for meetings given a group of persons.
     * Limited method signature of
     * suggestTimeslots(List<String> emailParticipants, int count,
     *                  List<MeetingDays> preferredDays, List<Integer> preferredHours)
     * - Invokes with defaultPreferredDays, defaultPreferredHours
     * @param emailParticipants list of strings of emails of participants for suggested meeting timeslot
     * @param count             int amount of number of timeslots suggested
     * @return                  ArrayList<LocalDateTime> output result of type LocalDateTime
     */
    public ArrayList<LocalDateTime> suggestTimeslots(List<String> emailParticipants, int count) {
        return suggestTimeslots(emailParticipants, count, defaultPreferredDays, defaultPreferredHours);
    }

    /**
     * Look up Person instance from given email String
     * @param email of String type
     * @return Person instance
     * @throws Exception if email does not match to any existing Person instance or is invalid.
     */
    private Person getPersonFromEmail(String email) throws Exception {
        if (email == null) {
            throw new InvalidParameterException("String email must be not-null");
        }
        if (isValidEmail(email)) {
            for (Person person : people) {
                if (person.getEmail().equals(email)) {
                    return person;
                }
            }
        }
        throw new Exception("Email: " + email + " does not exist.");
    }

    /**
     * Validates given String as valid email format using Regex pattern match
     * @param input email as String type
     * @return true if given String is valid email format
     */
    private boolean isValidEmail(String input) {
        String regexPattern = "^(.+)@(\\S+)$"; // email format
        return Pattern.compile(regexPattern).matcher(input).matches();
    }

    /**
     * Custom String format to LocalDateTime object, for creating such objects from simple input Strings.
     * E.g. "14-01-01-2024" returns 2024-01-01T14:00
     * Intentionally disregards minutes, seconds, etc. for the sake of whole hour timeslots.
     * @param input String with date pattern "HH-dd-MM-yyyy", using 24-hour count.
     * @return new LocalDateTime object corresponding to input string
     */
    private LocalDateTime formatStringToDatetime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-dd-MM-yyyy");
        return LocalDateTime.parse(input, formatter);
    }
}
