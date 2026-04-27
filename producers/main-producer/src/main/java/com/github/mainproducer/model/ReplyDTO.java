package com.github.mainproducer.model;

public record ReplyDTO<T>(String status, T payload, String error) {

    public static ReplyDTO<Void> error(String error) {
        return new ReplyDTO<>("ERROR", null, error);
    }

    public static <T> ReplyDTO<T> success(T payload) {
        return new ReplyDTO<>("SUCCESS", payload, null);
    }

    public static byte[] errorBytes(String error) {
        return error(error).toString().getBytes();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"status\":\"");
        sb.append(status);
        sb.append("\",\"payload\":\"");
        sb.append(payload);
        sb.append("\",\"error\":\"");
        sb.append(error);
        sb.append("\"}");
        return sb.toString();
    }
}
