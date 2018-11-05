package no.nav.tag.interessemelding;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Interessemelding {
    @Id
    private Integer id;
    private String fylke;
    private String kommune;
    private String bedriftsnavn;
    private String fornavn;
    private String etternavn;
    private String epost;
}
