package utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class YourClassTest {

    @Test
    public void testReadFile() {
        // Arrange
        String filePath = "C:\\Users\\ilya.krenev\\Desktop\\kurs_test\\exportedTeams.txt";
        int columnsAmount = 6;

        // Act
        Object[][] result;
        try {
            result = FileUtils.readFile(filePath, columnsAmount);
        } catch (IOException e) {
            fail("IOException was thrown");
            return;
        }

        // Assert
        assertNotNull(result);
        assertEquals(columnsAmount, result[0].length); // Assuming testFile.csv contains 3 rows

        // Additional assertions depending on what is expected from the file
        // YourClass.readFile implementation should be tested in isolation
    }
}