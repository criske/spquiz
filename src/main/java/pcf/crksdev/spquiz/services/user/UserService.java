package pcf.crksdev.spquiz.services.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pcf.crksdev.spquiz.data.user.UserEntity;
import pcf.crksdev.spquiz.data.user.UserRepository;
import pcf.crksdev.spquiz.services.user.context.AuthenticatedUser;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Optional;

public interface UserService {

    Optional<User> authenticated();

    Optional<User> getById(final String id);

    void register(final User user);
}

@Service
@Transactional
class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final User authenticated;

    UserServiceImpl(
        final UserRepository repository,
        @AuthenticatedUser final User authenticated
    ) {
        this.repository = repository;
        this.authenticated = authenticated;
    }

    @Override
    public Optional<User> authenticated() {
        boolean isPresent;
        try {
            isPresent = authenticated.getId() != null;
        } catch (UsernameNotFoundException exception) {
            isPresent = false;
        }
        return isPresent ? Optional.of(authenticated) : Optional.empty();
    }

    @Override
    public Optional<User> getById(final String id) {
        return this.repository.findById(id).map(this::entityToModel);
    }

    @Override
    public void register(final User user) {
        if (!this.repository.existsById(user.getId())) {
            this.repository.save(this.modelToEntity(user));
        }
    }

    private User entityToModel(final UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getPassword(),
            entity.getFullName(),
            entity.getEmail(),
            URI.create(entity.getAvatar())
        );
    }

    private UserEntity modelToEntity(final User model) {
        var entity = new UserEntity();
        entity.setId(model.getId());
        entity.setPassword(model.getPassword());
        entity.setFullName(model.getFullName());
        entity.setAvatar(model.getAvatar().toString());
        entity.setEmail(model.getEmail());
        return entity;
    }

}


