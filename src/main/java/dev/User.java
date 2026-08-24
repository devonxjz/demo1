package dev;

import java.io.Serializable;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String heardFrom;
    private String wantsUpdates;
    private String emailAnnouncements;
    private String contactBy;

    public User() {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.dateOfBirth = "";
        this.heardFrom = "Not specified";
        this.wantsUpdates = "No";
        this.emailAnnouncements = "No";
        this.contactBy = "Email or postal mail";
    }

    public User(String firstName, String lastName, String email, String dateOfBirth,
                String heardFrom, String wantsUpdates, String emailAnnouncements, String contactBy) {
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.dateOfBirth = dateOfBirth != null ? dateOfBirth.trim() : "";
        this.heardFrom = (heardFrom != null && !heardFrom.trim().isEmpty()) ? heardFrom.trim() : "Not specified";
        this.wantsUpdates = (wantsUpdates != null && !wantsUpdates.trim().isEmpty()) ? "Yes" : "No";
        this.emailAnnouncements = (emailAnnouncements != null && !emailAnnouncements.trim().isEmpty()) ? "Yes" : "No";
        this.contactBy = (contactBy != null && !contactBy.trim().isEmpty()) ? contactBy.trim() : "Email or postal mail";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHeardFrom() {
        return heardFrom;
    }

    public void setHeardFrom(String heardFrom) {
        this.heardFrom = heardFrom;
    }

    public String getWantsUpdates() {
        return wantsUpdates;
    }

    public void setWantsUpdates(String wantsUpdates) {
        this.wantsUpdates = wantsUpdates;
    }

    public String getEmailAnnouncements() {
        return emailAnnouncements;
    }

    public void setEmailAnnouncements(String emailAnnouncements) {
        this.emailAnnouncements = emailAnnouncements;
    }

    public String getContactBy() {
        return contactBy;
    }

    public void setContactBy(String contactBy) {
        this.contactBy = contactBy;
    }
}
