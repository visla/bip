package bip.utilities;

import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
public class NameUtil {
    public String makeKeyName(String path) {
        String extension = FilenameUtils.getExtension(path);
        UUID keyUid = UUID.randomUUID();
        String keyName = keyUid.toString() + "." + extension;
        return keyName;
    }

    public String updateKeyName(String keyName, String suffix) {
        String extension = FilenameUtils.getExtension(keyName);
        String name = FilenameUtils.getName(keyName);

        return name + '-' + suffix + "." + extension;
    }

}