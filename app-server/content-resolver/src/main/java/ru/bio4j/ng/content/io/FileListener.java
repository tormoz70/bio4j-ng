package ru.bio4j.ng.content.io;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/*
* Интерфейс позволяющий получать события об изменении контента
*
* */
public interface FileListener {

    public void onEvent(Path name, WatchEvent.Kind<Path> kind);
}
