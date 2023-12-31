package com.cosium.code.format;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Réda Housni Alaoui
 */
public abstract class AbstractModuleMavenGitCodeFormatMojo extends AbstractMavenGitCodeFormatMojo {

  /** Skip execution of this goal */
  @Parameter(property = "gcf.skip", defaultValue = "false")
  private boolean skip;

  @Parameter(property = "gcf.includedModules")
  private List<String> includedModules;

  @Parameter(property = "gcf.excludedModules")
  private List<String> excludedModules;

  /**
   * @return True if the goal is enabled for the current module
   */
  private boolean isEnabled() {
    List<String> modulesToExclude =
        Optional.ofNullable(this.excludedModules).orElse(Collections.emptyList());
    if (modulesToExclude.contains(artifactId())) {
      getLog().info(artifactId() + " is part of the excluded modules. Goal disabled.");
      return false;
    }

    List<String> modulesToInclude =
        Optional.ofNullable(this.includedModules).orElse(Collections.emptyList());
    if (!modulesToInclude.isEmpty() && !modulesToInclude.contains(artifactId())) {
      getLog().info(artifactId() + " is not part of defined included modules. Goal disabled.");
      return false;
    }

    if ((!modulesToInclude.isEmpty() || !modulesToExclude.isEmpty()) && isExecutionRoot()) {
      getLog()
          .info(
              "Explicit included or excluded modules defined and the current module is the execution root. Goal disabled.");
      return false;
    }

    getLog().debug("Goal enabled");
    return true;
  }

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("skipped");
      return;
    }
    if (!isEnabled()) {
      return;
    }
    doExecute();
  }

  protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
