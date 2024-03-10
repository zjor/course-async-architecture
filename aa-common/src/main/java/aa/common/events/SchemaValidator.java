package aa.common.events;

import aa.common.events.auth.v1.AccountCreated;
import aa.common.events.auth.v1.AccountDeleted;
import aa.common.events.auth.v1.AccountRoleChanged;
import aa.common.events.tasks.v1.TaskAssigned;
import aa.common.events.tasks.v1.TaskCompleted;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class SchemaValidator {

    private static final Map<String, Schema> schemaRegistry = new HashMap<>();

    static {
        schemaRegistry.put(AccountCreated.SCHEMA, load(AccountCreated.SCHEMA));
        schemaRegistry.put(AccountRoleChanged.SCHEMA, load(AccountRoleChanged.SCHEMA));
        schemaRegistry.put(AccountDeleted.SCHEMA, load(AccountDeleted.SCHEMA));

        schemaRegistry.put(TaskAssigned.SCHEMA, load(TaskAssigned.SCHEMA));
        schemaRegistry.put(TaskCompleted.SCHEMA, load(TaskCompleted.SCHEMA));
    }

    private static Schema load(String key) {
        var location = "events/schemas/" + key + ".json";
        try (var in = SchemaValidator.class.getClassLoader().getResourceAsStream(location)) {
            Objects.requireNonNull(in, "Resource is null: " + location);
            return SchemaLoader.load(new JSONObject(new JSONTokener(in)));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load schema for key: " + key, e);
        }
    }

    public static boolean isValid(String json, String schemaKey) {
        var validator = schemaRegistry.get(schemaKey);
        if (validator == null) {
            log.warn("No schema for the key: {}", schemaKey);
            return false;
        }
        try {
            validator.validate(new JSONObject(json));
            return true;
        } catch (ValidationException e) {
            var violations = e.getAllMessages().stream().collect(Collectors.joining(";"));
            log.warn("Schema validation failed: " + violations, e);
            return false;
        }
    }

}
