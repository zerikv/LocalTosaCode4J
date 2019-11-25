package com.zerikv.battledev;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Tests en local d'un challenge de la BattleDev. <br>
 * Conventions: <br>
 *     - la classe concrète de test doit être nommée comme la classe de la solution avec le suffixe Test. <br>
 *     - les fichiers des données de tests doivent être nommés inputN.txt et outputN.txt. <br>
 */
public abstract class LocalTestRunner {

    private static final Logger logger = LoggerFactory.getLogger(LocalTestRunner.class);

    private static final Pattern INPUT_TEST_FILE_PATTERN = Pattern.compile("^input\\d+\\.txt$");

    @TestFactory
    public Stream<DynamicTest> createTests() throws IOException {
        File testResDir = getTestResDir();
        return Files.list(testResDir.toPath()) //
                .filter(p -> INPUT_TEST_FILE_PATTERN.matcher(p.getFileName().toString()).find()) //
                .map(p -> DynamicTest.dynamicTest(p.getFileName().toString(), () -> runTest(p.toFile())));
    }

    /**
     * Exécution d'un test créé dynamiquement
     */
    private void runTest(File inputFile) {
        logger.debug("Running test {} with input {}", getClass().getName(), inputFile);
        // résultat attendu des tests
        String[] expectedResults = getExpectedResults(inputFile);
        logger.debug("expectedResults={}", expectedResults);

        ByteArrayOutputStream actualResultBytes = new ByteArrayOutputStream();
        InputStream sysin = System.in;
        PrintStream sysout = System.out;
        try (InputStream stdin = new FileInputStream(inputFile); PrintStream stdout = new PrintStream(actualResultBytes)) {
            Method maiMethod =  getMainMathod();
            // rediriger les entrées et les sorties standards
            System.setIn(stdin);
            System.setOut(stdout);
            // invoquer la méthode main() de la classe à tester
            maiMethod.invoke(null,  (Object)new String[0]);
        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
            // n'arrive jamais haha
            throw new RuntimeException(e);
        } finally {
            System.setIn(sysin);
            System.setOut(sysout);
        }

        // vérifier le résultat
        String[] actualResults = actualResultBytes.toString().split("\\r?\\n");
        logger.debug("actualResults={}", actualResults);
        assertArrayEquals(expectedResults, actualResults);
    }

    /**
     * Récupérer le répertoire des données de test.
     */
    private File getTestResDir() {
        String testResDirRelPath = "/" + removeTestSuffix(getClass().getName().replaceAll("\\.", "/"));
        URL testResDirUrl = getClass().getResource(testResDirRelPath);
        if (testResDirUrl == null) {
            throw new RuntimeException("Directory of data test not found: " + testResDirRelPath);
        }
        try {
            String testResDirPath = testResDirUrl.toURI().getPath();
            logger.debug("testResDirPath={}", testResDirPath);
            return new File(testResDirPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String removeTestSuffix(String s) {
        return s.substring(0, s.length() - "Test".length());
    }

    /**
     * Lecture des résultats attendus.
     */
    private String[] getExpectedResults(File inputFile) {
        File outputFile = getOutputFile(inputFile);
        try {
            return Files.readAllLines(outputFile.toPath()).toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupérer le fichier de résultat correspondant à un fichier d'entrée.
     */
    private File getOutputFile(File inputFile) {
        File outputFile = new File(inputFile.getParentFile(), inputFile.getName().replace("in", "out"));
        if (!outputFile.exists()) {
            throw new RuntimeException("Output file not found: " + outputFile);
        }
        return outputFile;
    }

    /**
     * Récupération de la méthode "main" de la classe à tester
     */
    private Method getMainMathod() {
        String testClassName = removeTestSuffix(getClass().getName());
        try {
            Class<?> testClass = Class.forName(testClassName);
            return testClass.getMethod("main", String[].class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
