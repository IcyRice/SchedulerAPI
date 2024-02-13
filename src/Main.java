import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Initiate Scheduler
        Scheduler scheduler = new Scheduler();

        // Creating persons
        try {
            scheduler.createPerson("Test0", "test0@person.com");
            scheduler.createPerson("Test1", "test1@person.com");
            scheduler.createPerson("Test2", "test2@person.com");
            scheduler.createPerson("Test3", "test3@person.com");
            scheduler.createPerson("Test4", "test4@person.com");
            scheduler.createPerson("Test5", "test5@person.com");
            scheduler.createPerson("Test6", "test6@person.com");
            scheduler.createPerson("Test7", "test7@person.com");
            scheduler.createPerson("Test8", "test8@person.com");
            scheduler.createPerson("Test9", "test9@person.com");
        } catch (Exception e) {
            System.out.println(e);
        }

        // Grouping some existing persons in lists, identified by email.
        List<String> testParticipants_0_1 = new ArrayList<>() {{
            add("test0@person.com");
            add("test1@person.com");
        }};
        List<String> testParticipants_2_3_4 = new ArrayList<>() {{
            add("test2@person.com");
            add("test3@person.com");
            add("test4@person.com");
        }};
        List<String> testParticipants_0_2 = new ArrayList<>() {{
            add("test0@person.com");
            add("test2@person.com");
        }};


        // String format for timeslots is "HH-dd-MM-yyyy"
        // HH denoting full hours of 24-hour clock: "09-21-02-2024" --> 2024-02-21T09:00
        // The following creates meetings on Feb. 21.
        try {
            scheduler.createMeeting("09-21-02-2024", testParticipants_0_1);
            scheduler.createMeeting("09-21-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("10-21-02-2024", testParticipants_0_2);
            scheduler.createMeeting("13-21-02-2024", testParticipants_0_2);
            scheduler.createMeeting("14-21-02-2024", testParticipants_0_2);

            scheduler.showScheduleForPerson("test0@person.com");
        } catch (Exception e) {
            System.out.println(e);
        }

        // Grouping 5 Persons for suggested timeslots
        List<String> testParticipants_0_1_2_3_4 = new ArrayList<>() {{
            add("test0@person.com");
            add("test1@person.com");
            add("test2@person.com");
            add("test3@person.com");
            add("test4@person.com");
        }};
        try {
            scheduler.suggestTimeslots(testParticipants_0_1_2_3_4, 10);
        } catch (Exception e) {
            System.out.println(e);
        }


        // String format for timeslots is "HH-dd-MM-yyyy"
        // HH denoting full hours of 24-hour clock: "09-14-02-2024" --> 2024-02-14T09:00
        // The following creates meetings on Feb. 14. and Feb. 15.
        try {
            scheduler.createMeeting("09-14-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("10-14-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("13-14-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("14-14-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("09-15-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("10-14-02-2024", testParticipants_2_3_4); //duplicate
            scheduler.createMeeting("10-15-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("13-15-02-2024", testParticipants_2_3_4);
            scheduler.createMeeting("14-15-02-2024", testParticipants_2_3_4);

            scheduler.suggestTimeslots(testParticipants_0_2, 5);
        } catch (Exception e) {
            System.out.println(e);
        }


        // Set to same as Scheduler.java current default fields.
        // --> Change these to test different preferred timeslots, which constrains suggestTimeslots().
        List<Scheduler.MeetingDays> days = new ArrayList<>() {{
            add(Scheduler.MeetingDays.MONDAY);
            add(Scheduler.MeetingDays.TUESDAY);
            add(Scheduler.MeetingDays.WEDNESDAY);
            add(Scheduler.MeetingDays.THURSDAY);
            add(Scheduler.MeetingDays.FRIDAY);
        }};
        List<Integer> hours = new ArrayList<>() {{
            add(9); add(10); add(13); add(14);
        }};
        try {
            // Running overloaded method with same input
            scheduler.suggestTimeslots(testParticipants_0_2); //default count = 1
            scheduler.suggestTimeslots(testParticipants_0_2, 5);
            scheduler.suggestTimeslots(testParticipants_0_2, 5, days, hours);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}


