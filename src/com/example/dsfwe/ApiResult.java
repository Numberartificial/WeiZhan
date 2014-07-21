package com.example.dsfwe;

public  class ApiResult<T> {
    private int error = 0;
    private String message;
    private T result;
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }
    public int getError() {
        return error;
    }
    public void setError(int error) {
        this.error = error;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}