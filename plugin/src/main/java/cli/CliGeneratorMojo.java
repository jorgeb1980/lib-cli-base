package cli;

import cli.annotations.Command;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;

import static java.nio.file.Files.readAttributes;
import static java.nio.file.Files.setPosixFilePermissions;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

@Mojo(name = "generate-files", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class CliGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    private String toString(InputStream is) {
        var ret = "";
        try {
            ret = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return ret;
    }

    private void makeExecutable(File f) throws IOException {
        try {
            java.nio.file.Path path = f.toPath();
            Set<PosixFilePermission> perms = readAttributes(path, PosixFileAttributes.class).permissions();

            perms.add(OWNER_WRITE);
            perms.add(OWNER_READ);
            perms.add(OWNER_EXECUTE);
            perms.add(GROUP_READ);
            perms.add(GROUP_EXECUTE);
            perms.add(OTHERS_READ);
            perms.add(OTHERS_EXECUTE);
            setPosixFilePermissions(path, perms);
        } catch (UnsupportedOperationException uoe) {
            getLog().warn(String.format("Unable to set permissions for %s - irrelevant if OS is Windows", f.getName()));
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        var pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
        var classRealm = pluginDescriptor.getClassRealm();
        var classes = new File(project.getBuild().getOutputDirectory());
        try {
            classRealm.addURL(classes.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Class clazz = Class.forName("cli.annotations.Command");
            // Scan for the annotated classes
            Set<Class<?>> commands = new Reflections().getTypesAnnotatedWith(clazz);
            for (Class<?> eachClazz: commands) {
                if (eachClazz != null) getLog().info(String.format("Generating scripts for %s", eachClazz));
            }
            File scriptsDir = new File(project.getBuild().getDirectory(), "redist/scripts");
            scriptsDir.mkdirs();
            for (String templateName: List.of("template.bat", "template.sh")) {
                var templateContent = toString(CliGeneratorMojo.class.getClassLoader().getResourceAsStream(templateName));
                for (Class<?> command : commands) {
                    var extension = templateName.substring(templateName.lastIndexOf(".") + 1);
                    // For every annotated class, create a file based on the template
                    var annotation = (Command) command.getAnnotation(clazz);
                    var commandName = annotation.command();
                    // Optimization: let the entry point get straight the class name,
                    //	we are making the work in advance!
                    var commandClass = command.getCanonicalName();
                    var jvmArgs = annotation.jvmArgs();
                    var script = new File(scriptsDir, commandName + "." + extension);
                    script.createNewFile();
                    var formattedText = templateContent.
                        replaceAll("<<COMMAND_CLASS>>", commandClass).
                        replaceAll("<<JVM_ARGS>>", jvmArgs.trim()).
                        replaceAll("<<START_COMMAND>>", annotation.isBackground() ? "start" : "").
                        replaceAll("<<JAVA_COMMAND>>", annotation.isBackground() ? "javaw" : "java");
                    Files.write(script.toPath(), formattedText.getBytes(StandardCharsets.UTF_8));
                    makeExecutable(script);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
