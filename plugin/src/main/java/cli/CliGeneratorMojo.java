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
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.util.IOUtil;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

@Mojo(name = "generate-files", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class CliGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "package")
    String packageName;

    private String toString(InputStream is) {
        String ret = "";
        try {
            ret = IOUtil.toString(is);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return ret;
    }

    private void makeExecutable(File f) throws IOException {
        try {
            java.nio.file.Path path = f.toPath();
            Set<java.nio.file.attribute.PosixFilePermission> perms =
                java.nio.file.Files.readAttributes(path, java.nio.file.attribute.PosixFileAttributes.class).permissions();

            perms.add(java.nio.file.attribute.PosixFilePermission.OWNER_WRITE);
            perms.add(java.nio.file.attribute.PosixFilePermission.OWNER_READ);
            perms.add(java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE);
            perms.add(java.nio.file.attribute.PosixFilePermission.GROUP_READ);
            perms.add(java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE);
            perms.add(java.nio.file.attribute.PosixFilePermission.OTHERS_READ);
            perms.add(java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE);
            java.nio.file.Files.setPosixFilePermissions(path, perms);
        } catch (UnsupportedOperationException uoe) {
            System.err.println(uoe.getMessage());
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        final PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
        final ClassRealm classRealm = pluginDescriptor.getClassRealm();
        final File classes = new File(project.getBuild().getOutputDirectory());
        try {
            classRealm.addURL(classes.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Class clazz = Class.forName("cli.annotations.Command");
            // Scan for the annotated classes
            Set<Class<?>> commands = new Reflections(packageName).getTypesAnnotatedWith(clazz);
            for (Class<?> eachClazz: commands) {
                System.out.println(eachClazz);
            }
            File scriptsDir = new File(project.getBuild().getDirectory(), "redist/scripts");
            scriptsDir.mkdirs();
            for (String templateName: List.of("template.bat", "template.sh")) {
                String templateContent = toString(CliGeneratorMojo.class.getClassLoader().getResourceAsStream(templateName));
                for (Class<?> command : commands) {
                    String extension = templateName.substring(templateName.lastIndexOf(".") + 1);
                    // For every annotated class, create a file based on the template
                    Command annotation = (Command) command.getAnnotation(clazz);
                    String commandName = annotation.command();
                    // Optimization: let the entry point get straight the class name,
                    //	we are making the work in advance!
                    String commandClass = command.getCanonicalName();
                    File script = new File(scriptsDir, commandName + "." + extension);
                    script.createNewFile();
                    String formattedText = templateContent.replaceAll("<<COMMAND_CLASS>>", commandClass);
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
