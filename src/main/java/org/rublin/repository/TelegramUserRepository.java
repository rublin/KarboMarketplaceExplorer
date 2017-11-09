package org.rublin.repository;

import org.rublin.model.TelegramUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TelegramUserRepository extends MongoRepository<TelegramUser, String> {

}
