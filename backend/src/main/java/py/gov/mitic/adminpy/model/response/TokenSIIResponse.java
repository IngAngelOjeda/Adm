package py.gov.mitic.adminpy.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenSIIResponse {

    private String token;

    private boolean success;
}
