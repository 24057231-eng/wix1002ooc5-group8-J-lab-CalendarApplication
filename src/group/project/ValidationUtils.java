package group.project;

/**
 * ValidationUtils
 *
 * Centralized validation rules to avoid duplicated checks across UI/BLL.
 */
public class ValidationUtils {

    /**
     * Validate fields for create/update.
     * Returns null if valid; otherwise returns a human-readable error message.
     */
    public static String validateEventForCreateOrUpdate(Event e) {
        if (e == null) return "Event is null.";

        // Required fields
        if (isBlank(e.get_title())) return "Title cannot be empty.";
        if (isBlank(e.get_start_time())) return "Start time cannot be empty.";
        if (isBlank(e.get_end_time())) return "End time cannot be empty.";

        // CSV safety (because DAL uses '|' as delimiter and attendees uses ',')
        if (containsPipe(e.get_title()) || containsPipe(e.get_description()) || containsPipe(e.get_location()) || containsPipe(e.get_category())) {
            return "Input cannot contain the '|' character (reserved as file delimiter).";
        }
        for (String a : e.get_attendees()) {
            if (a != null && a.contains(",")) {
                return "Attendee name cannot contain ',' (reserved as attendee delimiter).";
            }
            if (a != null && a.contains("|")) {
                return "Attendee name cannot contain '|' (reserved as file delimiter).";
            }
        }

        // Time format + order
        try {
            var s = EventManager.parseTime(e.get_start_time());
            var t = EventManager.parseTime(e.get_end_time());
            if (!s.isBefore(t)) return "Start time must be earlier than end time.";
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }

        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean containsPipe(String s) {
        return s != null && s.contains("|");
    }
}
