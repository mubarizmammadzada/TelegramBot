package com.example.tourappbot.repostiories;

import com.example.tourappbot.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRedisRepository extends CrudRepository<Session, String> {
}
