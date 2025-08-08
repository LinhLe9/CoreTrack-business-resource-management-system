package org.example.coretrack.dto.email;

public class TestEmailRequestDTO {
    private String toEmail;

    public TestEmailRequestDTO() {}

    public TestEmailRequestDTO(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
} 