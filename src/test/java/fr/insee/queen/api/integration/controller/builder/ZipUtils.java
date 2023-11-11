package fr.insee.queen.api.integration.controller.builder;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipFile;

public class ZipUtils {
    public ZipFile createZip(String resourcePath) throws IOException {
        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream output = new FileOutputStream(zip);
        InputStream zipInput = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourcePath));
        IOUtils.copy(zipInput, output);
        return new ZipFile(zip);
    }
}
