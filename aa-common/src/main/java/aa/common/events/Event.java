package aa.common.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event<T> {
    private String id;
    private String name;
    private int version;
    private long timestamp;
    private String producer;
    private T data;

    @JsonIgnore
    public String getSchemaKey() {
        return producer + "/v" + version + "/" + name;
    }
}
