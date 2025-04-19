package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Card {
    @Id
    private String id;
    private String name;
    private String supertype;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_subtypes", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "subtype")
    private List<String> subtypes;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_types", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "type")
    private List<String> types;
    private String number;
    private String rarity;
    @Embedded
    @JsonProperty("set")
    private SetInfo setInfo;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="small", column = @Column(name="image_small_url")),
            @AttributeOverride(name = "large", column = @Column(name = "image_large_url"))
    })
    @JsonProperty("images")
    private ImageInfo imageInfo;
    @Embedded
    @JsonProperty("cardmarket")
    private CardMarket cardMarket;
}
