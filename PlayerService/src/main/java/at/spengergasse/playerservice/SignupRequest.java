package at.spengergasse.playerservice;

public record SignupRequest (
        String username,
        String password
) { }

