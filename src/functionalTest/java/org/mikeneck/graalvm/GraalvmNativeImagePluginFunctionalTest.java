/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.mikeneck.graalvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * A simple functional test for the 'org.mikeneck.graalvm.greeting' plugin.
 */
public class GraalvmNativeImagePluginFunctionalTest {
    @Test public void runTaskOnJavaProject() throws IOException {
        // Setup the test build
        File projectDir = createProjectRoot("build/functionalTest/java");
        copyFile("java-project/build-gradle.txt", projectDir.toPath().resolve("build.gradle"));
        Path dir = projectDir.toPath().resolve("src/main/java/com/example");
        Files.createDirectories(dir);
        Path appJava = dir.resolve("App.java");
        copyFile("java-project/com_example_App_java.txt", appJava);

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","nativeImage", "--info", "--warning-mode", "all");
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        List<String> succeededTasks = result.tasks(TaskOutcome.SUCCESS).stream()
                .map(BuildTask::getPath)
                .collect(Collectors.toList());
        assertThat(succeededTasks, hasItems(":compileJava", ":classes", ":jar", ":nativeImage"));
        assertTrue(Files.exists(projectDir.toPath().resolve("build/native-image/test-app")));
        assertThat(result.getOutput(), 
                not(containsString("This behaviour has been deprecated and is scheduled to be removed in Gradle 7.0")));
    }

    @Test public void runTaskWithCustomOutputDirectory() throws IOException {
        // Setup the test build
        File projectDir = createProjectRoot("build/functionalTest/java-custom-output-directory");
        copyFile("java-project/build-gradle-output-directory.txt", projectDir.toPath().resolve("build.gradle"));
        Path dir = projectDir.toPath().resolve("src/main/java/com/example");
        Files.createDirectories(dir);
        Path appJava = dir.resolve("App.java");
        copyFile("java-project/com_example_App_java.txt", appJava);

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","nativeImage", "--stacktrace", "--warning-mode", "all");
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        assertThat(result.taskPaths(TaskOutcome.SUCCESS), hasItem(":nativeImage"));
        assertTrue(Files.exists(projectDir.toPath().resolve("build/executable/test-app")));
        assertThat(result.getOutput(),
                not(containsString("This behaviour has been deprecated and is scheduled to be removed in Gradle 7.0")));
    }

    @Test public void runTaskOnKotlinProject() {
        FunctionalTestContext context = new FunctionalTestContext("kotlin-project");
        context.setup();
        Path projectDir = context.rootDir;

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","nativeImage", "--warning-mode", "all");
        runner.withProjectDir(projectDir.toFile());
        BuildResult result = runner.build();

        List<String> succeededTasks = result.tasks(TaskOutcome.SUCCESS).stream()
                .map(BuildTask::getPath)
                .collect(Collectors.toList());
        assertThat(succeededTasks, hasItems(":compileKotlin", ":inspectClassesForKotlinIC", ":jar", ":nativeImage"));
        assertTrue(Files.exists(projectDir.resolve("build/native-image/test-app")));
        assertThat(result.getOutput(),
                not(containsString("This behaviour has been deprecated and is scheduled to be removed in Gradle 7.0")));
    }

    @Test
    public void runTaskOnKotlinProjectImprovedDsl() {
        FunctionalTestContext context = new FunctionalTestContext("kotlin-project-dsl-improved");
        context.setup();
        Path projectDir = context.rootDir;

        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("clean","nativeImage", "--warning-mode", "all");
        runner.withProjectDir(projectDir.toFile());
        BuildResult result = runner.build();

        List<String> succeededTasks = result.tasks(TaskOutcome.SUCCESS).stream()
                .map(BuildTask::getPath)
                .collect(Collectors.toList());
        assertThat(succeededTasks, hasItems(":compileKotlin", ":inspectClassesForKotlinIC", ":jar", ":nativeImage"));
        assertTrue(Files.exists(projectDir.resolve("build/image/test-app")));
        assertThat(result.getOutput(),
                not(containsString("This behaviour has been deprecated and is scheduled to be removed in Gradle 7.0")));
    }

    static File createProjectRoot(String s) throws IOException {
        File projectDir = new File(s);
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        return projectDir;
    }

    static void copyFile(String resourceName, Path file) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(resourceName);
        if (url == null) {
            throw new FileNotFoundException(resourceName);
        }
        Files.deleteIfExists(file);
        try (final InputStream inputStream = loader.getResourceAsStream(resourceName)) {
            Files.copy(Objects.requireNonNull(inputStream), file);
        }
    }

    static void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
