
import java.util.*;
import java.time.LocalDate;

public class Event {
    Map<String, Object> eventInfo = new HashMap<>();

    public Event() {

    }

    public Event(List<Object> eventValues) {
        if (!(eventValues == null || eventValues.size() == 0)) {
            for (int i = 0; i < eventValues.size(); i++) {
                eventInfo.put(CommandUtil.getEventColumns()[i], eventValues.get(i));
            }
        }
    }

    public String getName() {
        return (String) this.eventInfo.get("Name-s");
    }

    @Override
    public String toString() {
        return (String) this.eventInfo.get("Name-s") + " --> " + getDay() + " ~ " + getTime() + " ~ " + getAttending()
                + " ~ " + getPriority();
    }

    public String getPriority() {
        int priority = (Integer) this.eventInfo.get("Priority-i") - 1;
        String opt[] = { "Low", "Med", "High", "Very High" };
        return priority >= opt.length || priority < 0 ? "Low" : opt[priority];
    }

    public String getAttending() {
        int attend = (Integer) this.eventInfo.get("Status-i") - 1;
        String opt[] = { "Declined", "Attending", "Tentative", "Busy" };
        return attend >= opt.length || attend < 0 ? "Declined" : opt[attend];
    }

    public String getTime() {
        int start = (Integer) this.eventInfo.get("StartTime-i");
        int startH = start / 100;
        int startM = start % 100;

        int end = (Integer) this.eventInfo.get("EndTime-i");
        int endH = end / 100;
        int endM = end % 100;

        String starting = (startH == 0 ? "00" : startH) + ":" + (startM == 0 ? "00" : startM)
                + (startH >= 12 ? "pm" : "am");
        String ending = (endH == 0 ? "00" : endH) + ":" + (endM == 0 ? "00" : endM) + (endH >= 12 ? "pm" : "am");
        return "[" + starting + " - " + ending + "]";
    }

    public String getDay() {
        String date = (String) this.eventInfo.get("Date-s");
        String _date[] = date.split("-");
        LocalDate localDate = LocalDate.of(Integer.parseInt(_date[2]), Integer.parseInt(_date[0]),
                Integer.parseInt(_date[1]));
        java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        String month[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        String output = "[" + dayOfWeek.toString().substring(0, 3) + ", " + month[Integer.parseInt(_date[0]) - 1] + " "
                + _date[1] + ", " + _date[2] + "]";
        return output;
    }

}
