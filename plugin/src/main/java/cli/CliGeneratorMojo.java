package cli;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

@Mojo(name = "generate-files", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class CliGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "package")
    String packageName;

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
            Set<Class<?>> commands =
                new org.reflections.Reflections(packageName).getTypesAnnotatedWith(clazz);
            for (Class<?> eachClazz: commands) {
                System.out.println(eachClazz);
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
}
