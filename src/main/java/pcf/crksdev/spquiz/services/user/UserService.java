package pcf.crksdev.spquiz.services.user;

import org.springframework.stereotype.Service;
import pcf.crksdev.spquiz.data.user.UserEntity;
import pcf.crksdev.spquiz.data.user.UserRepository;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Optional;

public interface UserService {

    Optional<SpquizUser> getById(final String id);

    void register(final DefaultSpquizUser user);
}

@Service
@Transactional
class UserServiceImpl implements UserService {

    private final UserRepository repository;

    UserServiceImpl(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SpquizUser> getById(final String id) {
        return this.repository.findById(id).map(this::entityToModel);
    }

    @Override
    public void register(final DefaultSpquizUser user) {
        if (!this.repository.existsById(user.getId())) {
            this.repository.save(this.modelToEntity(user));
        }
    }

    private DefaultSpquizUser entityToModel(final UserEntity entity) {
        return new DefaultSpquizUser(
            entity.getId(),
            entity.getPassword(),
            entity.getFullName(),
            entity.getEmail(),
            URI.create(entity.getAvatar())
        );
    }

    private UserEntity modelToEntity(final SpquizUser model) {
        var entity = new UserEntity();
        entity.setId(model.getId());
        entity.setPassword(model.getPassword());
        entity.setFullName(model.getFullName());
        entity.setAvatar(model.getAvatar().toString());
        entity.setEmail(model.getEmail());
        return entity;
    }

}


