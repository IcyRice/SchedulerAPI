Repository for project for programming exercise: designing and implementing the API for a minimal scheduling application.

Given the fairly open-ended phrasing of the assignment, some liberties have been taken in design decisions of this:

* String based custom input format for creating objects of LocalDateTime-type.

^This was in order to enable various text input interactions, and using a "HH-dd-MM-yyyy" -format to have hours and dates, but not smaller time units. See how this is used in Main.java when calling scheduler.createMeeting()


* Given lists of specific preferred days of the week and specific preferred hours used to constrain suggested timeslots for meetings.

^The idea was to add some bias as to what are reasonable timeslots, instead of just outputting the next available arbitrary hour. This version only uses this generally in Scheduler.java, but a future version might add a feature of each individual Person having their own set of preferred meeting timeslots, which could be regarded when planning.

---

For further documentation, please refer to the javadoc-style commenting in SchedularAPI/src/Schedular.java

Scheduler.java is used by Main.java to run a simple scripted console demo.
Feel free to try different things in the main method when interacting with Scheduler. In order to not spend too much additional time on the assignment, I've omitted to implement unit tests. But I have considered a fair few potential fail-cases, that might be challenged.

* Note that the suggestTimeslots-method bases its initial suggestion on the 'currentTime' field set to 'LocalDateTime.now()'. It looks at today, and tries to suggest timeslots starting tomorrow at index 0 of 'preferredHours'.

---


Public methods in Scheduler.java:

```
Scheduler()
createPerson(String name, String email)
createMeeting(String stringTimeSlot, List<String> emailParticipants)
showScheduleForPerson(String email)
~showScheduleForPerson(Person person)~
getScheduleForPerson(String email)
~getScheduleForPerson(Person person)~
suggestTimeslots(List<String> emailParticipants, int count, List<MeetingDays> preferredDays, List<Integer> preferredHours)
suggestTimeslots(List<String> emailParticipants, int count)
suggestTimeslots(List<String> emailParticipants)
```

The public methods taking a 'Person' as param might be disregarded since 'Person' is not supposed to be accessible externally.

---

-- Instructions followed from Deltek: --

Exercise: Scheduling App

Design and implement the API for a minimal scheduling application. This API should be able to handle the following requirements:


1) Create persons with a name and unique email.

2) Create meetings involving one or more persons at a given time slot.

3) A meeting can only start at the hour mark and only last exactly one hour.

4) Show the schedule, i.e., the upcoming meetings, for a given person.

5) Suggest one or more available timeslots for meetings given a group of persons.


You should not implement a GUI, a simple unit test or console demo should suffice. Also please try to keep the number of third party libraries to a minimum. Finally, please do not spend time on storing data in files or databases.

The exercise should be solved in Java, Scala or C++.
