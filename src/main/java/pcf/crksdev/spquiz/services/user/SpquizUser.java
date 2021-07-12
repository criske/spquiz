package pcf.crksdev.spquiz.services.user;

import java.net.URI;

public interface SpquizUser {

    String getId();

    String getFullName();

    String getEmail();

    URI getAvatar();

    String getPassword();
}
