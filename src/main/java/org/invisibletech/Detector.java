package org.invisibletech;

import jep.Interpreter;
import jep.JepConfig;
import jep.SharedInterpreter;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

@CommandLine.Command(name = "detect", version = "Detector 1.0", mixinStandardHelpOptions = true)
public class Detector implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Detector.class.getName());

    // Since this is required we cannot try to run without this option and so don't need validations.
    @CommandLine.Option(names = {"-s", "--search-terms"}, description = "Words and phrases used for labels.", required = true, paramLabel = "N", split = ",")
    List<String> searchTerms;

    @CommandLine.Parameters(paramLabel = "<image file path>", description = "Image file for object detection.")
    String imagePath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Detector()).execute(args);
        System.exit(exitCode);
    }

    private static String deployPythons() {
        try {
            // Auto provision the pythons to a temp folder. Since you wouldn't ship your source folder.
            String pythonsAbsolutePath = Files.createTempDirectory("jep_pythons_deployment").toFile().getAbsolutePath();

            Path owlsPythonFile = Path.of(pythonsAbsolutePath, "owls_not_what_they_seem.py");

            if (Files.exists(owlsPythonFile)) {
                // Make sure we have the current Python file.
                Files.delete(owlsPythonFile);
            }

            InputStream pythonToDeploy = Detector.class.getResourceAsStream("/pythons/owls_not_what_they_seem.py");
            Files.copy(pythonToDeploy, owlsPythonFile);
            return pythonsAbsolutePath;
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to deploy pythons.", ioException);
        }
    }

    @Override
    public void run() {
        // set path for jep executing python
        String pythonsAbsolutePath = deployPythons();
        LOGGER.info("Deployed Python files to path: " + pythonsAbsolutePath);

        // set path for python docs with python script to run
        jep.JepConfig jepConf = new JepConfig();
        jepConf.addIncludePaths(pythonsAbsolutePath);
        jepConf.redirectStdout(System.out);
        jepConf.redirectStdErr(System.err);
        SharedInterpreter.setConfig(jepConf);

        try (Interpreter interp = new SharedInterpreter()) {
            LOGGER.info("Running detection in Python.");

            interp.eval("import owls_not_what_they_seem as owls");
            interp.set("search_terms", searchTerms);
            interp.set("image_path", imagePath);
            interp.eval("owls_detected = owls.beach_check(search_terms, image_path)");
            LOGGER.info("Class of detection is: " + interp.getValue("owls_detected").getClass());
            LOGGER.info("Detection results: " + interp.getValue("owls_detected"));
        }
    }
}
