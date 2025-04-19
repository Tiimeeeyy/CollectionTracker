package api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import data.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWrapper {
    private Card data;
}
