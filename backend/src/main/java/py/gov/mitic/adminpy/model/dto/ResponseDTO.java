package py.gov.mitic.adminpy.model.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private Integer code;
    private String message;
    private Object data;

    public ResponseDTO(Date timestamp, Integer code, String message, Object data) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(String message, HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.message = message;
    }

    public ResponseDTO(Object data, HttpStatus httpStatus) {
        this.data = data;
        this.code = httpStatus.value();
    }

    public HttpStatus getStatus() {
        HttpStatus status = null;
        switch (code) {
            case 200: status = HttpStatus.OK; break;
            case 400: status = HttpStatus.BAD_REQUEST; break;
            case 401: status = HttpStatus.UNAUTHORIZED; break;
            case 403: status = HttpStatus.FORBIDDEN; break;
            case 500: status = HttpStatus.INTERNAL_SERVER_ERROR; break;
        }
        return status;
    }

    public ResponseEntity<ResponseDTO> build() {
        ResponseDTO dto = new ResponseDTO(this.timestamp, this.code, this.message, this.data);
        return new ResponseEntity<>(dto, getStatus());
    }
}
