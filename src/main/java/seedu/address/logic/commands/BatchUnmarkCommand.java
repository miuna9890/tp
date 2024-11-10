package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.AttendanceCount;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Sex;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;

/**
 * Unmarks the attendance of all students in the current list.
 */
public class BatchUnmarkCommand extends Command {
    public static final String COMMAND_WORD = "batch-unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unmarks the attendance of all students in the current list";

    public static final String MESSAGE_BATCH_UNMARK_SUCCESS = "Unmarked attendance for: %1$s";
    public static final String MESSAGE_BATCH_UNMARK_NO_STUDENT_LIST = "There is no student in this list";

    private boolean hasStudent;
    private List<Student> students = new ArrayList<>();
    public BatchUnmarkCommand() {
        this.hasStudent = false;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        for (Person p : lastShownList) {
            if (p instanceof Student) {
                this.hasStudent = true;
                Student studentToUnmark = (Student) p;
                students.add(studentToUnmark);
            }
        }

        if (!this.hasStudent) {
            throw new CommandException(String.format(MESSAGE_BATCH_UNMARK_NO_STUDENT_LIST));
        }

        for (Student p : students) {
            Student markedStudent = createNewStudentWithUnmarkedAttendance(p);
            model.setPerson(p, markedStudent);
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String selectedPersons = formatUnmarkedStudents(students);

        return new CommandResult(String.format(MESSAGE_BATCH_UNMARK_SUCCESS, selectedPersons));
    }

    /**
     * Creates and returns a {@code Student} with decremented AttendanceCount.
     */
    public Student createNewStudentWithUnmarkedAttendance(Student studentToUnmark) {
        assert studentToUnmark != null;

        this.hasStudent = true;
        Name name = studentToUnmark.getName();
        Sex sex = studentToUnmark.getSex();
        Role role = studentToUnmark.getRole();
        Phone phone = studentToUnmark.getPhone();
        Email email = studentToUnmark.getEmail();
        Address address = studentToUnmark.getAddress();
        Set<Tag> tags = studentToUnmark.getTags();
        AttendanceCount currentAttendanceCount = studentToUnmark.getAttendanceCount();
        Integer currentAttendanceCountInt = currentAttendanceCount.integerCount();
        if (currentAttendanceCountInt == 0) {
            return studentToUnmark;
        }
        Integer newAttendanceCountInt = studentToUnmark.getAttendanceCount().integerCount() - 1;
        String newAttendanceCountStr = newAttendanceCountInt.toString();
        AttendanceCount newAttendanceCount = new AttendanceCount(newAttendanceCountStr);
        return new Student(name, sex, role, phone, email, address, tags, newAttendanceCount);
    }

    /**
     * Formats a list of student names into a comma-separated string for display.
     *
     * @param students The list of students unmarked.
     * @return A comma-separated string of student names, or "none" if the list is empty.
     */
    public static String formatUnmarkedStudents(List<Student> students) {
        return students.stream()
                .map(person -> person.getName().toString())
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("none");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }

}