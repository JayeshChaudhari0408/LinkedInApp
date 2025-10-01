package com.linkedin.user_service.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    //hash password for first time
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword,BCrypt.gensalt());
    }

    //Check that a plain text password matches the previously hashed password
    public static boolean checkPassword(String plainTextPassword, String hashPassword) {
        return BCrypt.checkpw(plainTextPassword,hashPassword);
    }
}
