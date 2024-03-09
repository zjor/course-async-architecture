package aa.common.events;

import aa.common.events.auth.v1.AccountCreated;
import org.junit.Assert;
import org.junit.Test;

public class SchemaValidatorTest {

    @Test
    public void shouldValidateSchema4AccountCreatedV1() {
        var json = """
                {
                    "id": 1,
                    "login": "alice",
                    "role": "ACCOUNTANT",
                    "created_at": "2024-03-09T21:03:42.423265Z"
                }
                """;
        Assert.assertTrue(SchemaValidator.isValid(json, AccountCreated.SCHEMA));
    }

    @Test
    public void shouldFailValidation4AccountCreatedV1() {
        Assert.assertFalse(SchemaValidator.isValid("{}", AccountCreated.SCHEMA));
    }

}