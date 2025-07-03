package school.hei.stored.endpoint.rest.controller.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@RestController
public class StoredIntController {
    private static final String FILE_PATH = "/tmp/stored-int.txt";
    private static final Random random = new Random();

    @GetMapping("/stored-int")
    public StoredIntResponse getStoredInt() {
        try {
            File file = new File(FILE_PATH);

            if (file.exists()) {
                try {
                    String content = Files.readString(Paths.get(FILE_PATH));
                    int storedNumber = Integer.parseInt(content.trim());
                    return new StoredIntResponse(storedNumber, "existing");
                } catch (Exception e) {
                    return handleError("Error reading existing file", e);
                }
            } else {
                return createNewFileWithRandomNumber();
            }
        } catch (Exception e) {
            return handleError("Unexpected error", e);
        }
    }

    private StoredIntResponse createNewFileWithRandomNumber() {
        int randomNumber = random.nextInt(1000000);
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(String.valueOf(randomNumber));
            return new StoredIntResponse(randomNumber, "new");
        } catch (IOException e) {
            return new StoredIntResponse(randomNumber, "fallback");
        }
    }

    private StoredIntResponse handleError(String message, Exception e) {
        int randomNumber = random.nextInt(1000000);
        System.err.println(message + ": " + e.getMessage());
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(String.valueOf(randomNumber));
            return new StoredIntResponse(randomNumber, "error_recovery");
        } catch (IOException ex) {
            return new StoredIntResponse(randomNumber, "fallback");
        }
    }

    public static class StoredIntResponse {
        private final int number;
        private final String status;

        public StoredIntResponse(int number, String status) {
            this.number = number;
            this.status = status;
        }

        public int getNumber() {
            return number;
        }

        public String getStatus() {
            return status;
        }
    }
}