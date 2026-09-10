package dev.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @Builder.Default
    private String username = "";

    @Column(name = "password")
    @Builder.Default
    private String password = "";

    @Column(name = "first_name")
    @Builder.Default
    private String firstName = "";

    @Column(name = "last_name")
    @Builder.Default
    private String lastName = "";

    @Column(name = "email", nullable = false)
    @Builder.Default
    private String email = "";

    @Column(name = "date_of_birth")
    @Builder.Default
    private String dateOfBirth = "";

    @Column(name = "heard_from")
    @Builder.Default
    private String heardFrom = "Not specified";

    @Column(name = "wants_updates")
    @Builder.Default
    private String wantsUpdates = "No";

    @Column(name = "email_announcements")
    @Builder.Default
    private String emailAnnouncements = "No";

    @Column(name = "contact_by")
    @Builder.Default
    private String contactBy = "Email or postal mail";

    public User(String firstName, String lastName, String email, String dateOfBirth,
                String heardFrom, String wantsUpdates, String emailAnnouncements, String contactBy) {
        this.username = "";
        this.password = "";
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.dateOfBirth = dateOfBirth != null ? dateOfBirth.trim() : "";
        this.heardFrom = (heardFrom != null && !heardFrom.trim().isEmpty()) ? heardFrom.trim() : "Not specified";
        this.wantsUpdates = (wantsUpdates != null && !wantsUpdates.trim().isEmpty()) ? "Yes" : "No";
        this.emailAnnouncements = (emailAnnouncements != null && !emailAnnouncements.trim().isEmpty()) ? "Yes" : "No";
        this.contactBy = (contactBy != null && !contactBy.trim().isEmpty()) ? contactBy.trim() : "Email or postal mail";
    }

    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username != null ? username.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.password = password != null ? password : "";
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.dateOfBirth = "";
        this.heardFrom = "Not specified";
        this.wantsUpdates = "No";
        this.emailAnnouncements = "No";
        this.contactBy = "Email or postal mail";
    }

    public String getDisplayName() {
        if (firstName != null && !firstName.trim().isEmpty()) {
            return firstName.trim();
        }
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        return "User";
    }
}
