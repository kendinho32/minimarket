package com.api.market.payload;

/**
 * Created by knavarro on 15/06/18.
 */
public class ApiResponse {
    private Boolean success;
    private String message;
    private Object data;

    public ApiResponse() {
    	super();
		this.message = null;
		this.data = null;
    }
    
    public ApiResponse(boolean success, String message, Object data) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
    }
    
    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static ApiResponse getErrorJsonRespone(String message)
	{
		return new ApiResponse(false, message);
	}
    
	public static ApiResponse getSuccessJsonRespone(String message, Object data)
	{
		return new ApiResponse(true, message, data);
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
    
    public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
