package com.student.emailtool.model;

public class Contact {
    private String name;
    private String email;
    private String city;

    public Contact() {
    }

    public Contact(String name, String email, String city) {
        this.name = name;
        this.email = email;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String toCsvLine(String format) {
        String normalizedFormat = format == null ? "formal" : format;

        String outName = name == null ? "" : name;
        String outEmail = email == null ? "" : email;
        String outCity = city == null ? "" : city;

        if ("lower".equals(normalizedFormat)) {
            outName = outName.toLowerCase();
            outEmail = outEmail.toLowerCase();
            outCity = outCity.toLowerCase();
        } else {
            outName = capitalizeWords(outName);
            outEmail = outEmail.toLowerCase();
            outCity = capitalizeWords(outCity);
        }

        return String.join(",", outName, outEmail, outCity);
    }

    private String capitalizeWords(String value) {
        String[] words = value.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1).toLowerCase())
              .append(" ");
        }
        return sb.toString().trim();
    }
}
