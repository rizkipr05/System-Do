package com.doapp.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: java ... PasswordHashGenerator <plain_password> [strength]");
      System.exit(1);
    }

    String plain = args[0];
    int strength = 10;
    if (args.length > 1) {
      try {
        strength = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        System.err.println("Strength must be an integer. Using default 10.");
      }
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);
    String hash = encoder.encode(plain);
    System.out.println(hash);
  }
}
