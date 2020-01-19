package rcs.auth.repositories;

import org.springframework.data.repository.CrudRepository;
import rcs.auth.models.db.UserCredentials;

public interface UserCredentialsRepository extends CrudRepository<UserCredentials, String>, UserCredentialsRepositoryCustom {
}