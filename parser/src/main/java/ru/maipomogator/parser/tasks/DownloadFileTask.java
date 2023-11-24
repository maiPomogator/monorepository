package ru.maipomogator.parser.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

/**
 * Callable для асинхронного скачивания файла
 */
public class DownloadFileTask implements Callable<File> {

    /**
     * Адрес файла, который нужно загрузить
     */
    private String srcUrl;
    /**
     * Путь, куда будет сохранен загруженный файл
     */
    private final Path targetPath;

    public DownloadFileTask(String srcUrl, Path targetPath) {
        this.srcUrl = srcUrl;
        this.targetPath = targetPath;
    }

    /**
     * Загружает файл.
     * 
     * @return Файл, содержащий загруженные данные.
     * @throws IOException        Если произошла ошибка ввода-вывода во время загрузки файла.
     * @throws URISyntaxException
     */
    @Override
    public File call() throws IOException, URISyntaxException {
        URL sourceUrl = new URI(srcUrl).toURL();
        try (ReadableByteChannel inChannel = Channels.newChannel(sourceUrl.openStream());
                FileChannel outChannel = FileChannel.open(targetPath, StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
        }
        return targetPath.toFile();
    }
}
