package pcf.crksdev.spquiz.services.user;

import java.net.URI;

public class User {

    private final String id;

    private final String fullName;

    private final String email;

    private final URI avatar;

    private final String password;

    public User(
        String id,
        String password,
        String fullName,
        String email,
        URI avatar
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.avatar = avatar;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public URI getAvatar() {
        return avatar;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", avatar=" + avatar +
            '}';
    }
}
