package api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import data.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResponseWrapper {
    private List<Card> data;
}
