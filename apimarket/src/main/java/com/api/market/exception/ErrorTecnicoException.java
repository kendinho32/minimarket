package com.api.market.exception;

public class ErrorTecnicoException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4252060205303705394L;
	private String mensaje;
    private String codigo;
    private String stacktrace;

    public ErrorTecnicoException(String msg, String cod, Throwable e) {
        super(msg,e);
        this.setMensaje(msg);
        this.setCodigo(cod);
        this.setStacktrace(e.getStackTrace().toString());
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
}
