package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    private String id;
    private String name;
    private String supertype;
    private List<String> subtypes;
    private List<String> types;
    private String number;
    private String rarity;
    @JsonProperty("set")
    private SetInfo setInfo;
    @JsonProperty("images")
    private ImageInfo imageInfo;
    @JsonProperty("cardmarket")
    private CardMarket cardMarket;
}
