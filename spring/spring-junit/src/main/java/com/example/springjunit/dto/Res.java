package com.example.springjunit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Res {
    private int result;
    private Body response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body{
        private String resultCode = "OK";
    }
}
