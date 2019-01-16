package com.api.market.error.handler;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase definida con JsonProperty la cual va a mostrar la informacion
 * cuando ocurre un error capturado por el errorHandler
 * 
 * @author knavarro <knavarro@everis.com>
 *
 */
public class ErrorInfo {

    @JsonProperty("message")
    private String message;
    @JsonProperty("status_code")
    private int statusCode;

    public ErrorInfo(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
