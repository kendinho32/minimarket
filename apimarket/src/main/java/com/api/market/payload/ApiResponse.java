package com.api.market.payload;

/**
 * Created by knavarro on 15/06/18.
 */
public class ApiResponse {
    private Boolean success;
    private String message;

    public ApiResponse() {}
    
    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
