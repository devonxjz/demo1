package dev.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.dateOfBirth = dateOfBirth != null ? dateOfBirth.trim() : "";
        this.heardFrom = (heardFrom != null && !heardFrom.trim().isEmpty()) ? heardFrom.trim() : "Not specified";
        this.wantsUpdates = (wantsUpdates != null && !wantsUpdates.trim().isEmpty()) ? "Yes" : "No";
        this.emailAnnouncements = (emailAnnouncements != null && !emailAnnouncements.trim().isEmpty()) ? "Yes" : "No";
        this.contactBy = (contactBy != null && !contactBy.trim().isEmpty()) ? contactBy.trim() : "Email or postal mail";
    }
}
