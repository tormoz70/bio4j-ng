package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.User;

public interface BioModule extends BioService {
    String getKey();
    String getDescription();
}
