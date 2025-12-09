package org.gmi.ecommerceproject.Exception;

import java.io.Serial;

public class APIException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public APIException(String message){
        super(message);
    }
}
