package et.gov.ethgov.workflow.domain.definition;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class WorkflowStep {

    private static final Pattern STEP_KEY = Pattern.compile("^[a-z][a-z0-9-]{1,31}$");

    private final String stepKey;
    private final String name;
    private final StepType type;
    private final String assigneeRole;
    private final int ordinal;

    private WorkflowStep(String stepKey, String name, StepType type, String assigneeRole, int ordinal) {
        this.stepKey = stepKey;
        this.name = name;
        this.type = type;
        this.assigneeRole = assigneeRole;
        this.ordinal = ordinal;
    }

    public static WorkflowStep of(
            String stepKey, String name, StepType type, String assigneeRole, int ordinal) {
        Objects.requireNonNull(stepKey, "stepKey");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        if (ordinal < 0) {
            throw new IllegalArgumentException("ordinal must be >= 0");
        }
        String key = stepKey.trim().toLowerCase(Locale.ROOT);
        if (!STEP_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid stepKey: " + stepKey);
        }
        String normalizedName = name.trim().replaceAll("\\s+", " ");
        if (normalizedName.length() < 2 || normalizedName.length() > 120) {
            throw new IllegalArgumentException("step name length must be between 2 and 120");
        }
        String role = null;
        if (assigneeRole != null && !assigneeRole.isBlank()) {
            role = assigneeRole.trim().toLowerCase(Locale.ROOT);
            if (role.length() < 2 || role.length() > 64) {
                throw new IllegalArgumentException("assigneeRole length must be between 2 and 64");
            }
        }
        return new WorkflowStep(key, normalizedName, type, role, ordinal);
    }

    public String stepKey() {
        return stepKey;
    }

    public String name() {
        return name;
    }

    public StepType type() {
        return type;
    }

    public Optional<String> assigneeRole() {
        return Optional.ofNullable(assigneeRole);
    }

    public int ordinal() {
        return ordinal;
    }
}
