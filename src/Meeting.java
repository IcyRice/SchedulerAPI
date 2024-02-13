import java.time.LocalDateTime;
import java.util.ArrayList;

public class Meeting {

    // For database integration: add ID field as primary key
    private final LocalDateTime timeSlot;
    private final ArrayList<Person> participants;


    public Meeting(LocalDateTime timeSlot, ArrayList<Person> participants) {
        this.timeSlot = timeSlot;
        this.participants = participants;
    }

    public void print() {
        System.out.println("Displaying Meeting at: "+timeSlot+" - "+timeSlot.getDayOfWeek());
        System.out.println("    Participants:");
        for(Person person : participants) {
            System.out.println("    "+person.getName()+"|"+person.getEmail());
        }
    }

    public LocalDateTime getTimeslot() {
        return timeSlot;
    }

    public void addParticipant(Person p) {
        participants.add(p);
    }

    public ArrayList<Person> getParticipants(){
        return participants;
    }
}
