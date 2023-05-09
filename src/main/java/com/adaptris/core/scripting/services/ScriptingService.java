package com.adaptris.core.scripting.services;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.validation.constraints.NotBlank;
import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.fs.FsWorker;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Supports arbitary scripting languges that are supported by JSR223.
 * <p>
 * You should take care when configuring this class; it can present an audit trail issue when used in combination with
 * {@link com.adaptris.core.services.dynamic.DynamicServiceExecutor} if your script executes arbitrary system commands. In that
 * situation, all commands will be executed with the current users permissions and may be subject to the virtual machines security
 * manager.
 * </p>
 * <p>
 * The script is executed and the AdaptrisMessage that is due to be processed is bound against the key "message" and an instance
 * of org.slf4j.Logger is also bound to key "log". These can be used as a standard variable within the script.
 * </p>
 * <p>
 * Note that this class can be used as the selector as part of a {@link com.adaptris.core.BranchingServiceCollection}. If used as
 * such, then you need
 * to remember to invoke {@link com.adaptris.core.AdaptrisMessage#setNextServiceId(String)} as part of the script and {@link
 * #setBranchingEnabled(Boolean)}
 * should be true.
 * <p>
 * 
 * @config scripting-service
 * 
 * 
 * @author lchan
 * 
 */
@XStreamAlias("scripting-service")
@AdapterComponent
@ComponentProfile(summary = "Execute a JSR223 script stored on the filesystem", tag = "service,scripting", branchSelector = true)
@DisplayOrder(order = {"language", "scriptFilename", "branchingEnabled"})
public class ScriptingService extends ScriptingServiceImp {

  @NotBlank
  private String scriptFilename;
  public ScriptingService() {
    super();
  }

  public ScriptingService(String uniqueId) {
    this();
    setUniqueId(uniqueId);
  }


  public String getScriptFilename() {
    return scriptFilename;
  }

  /**
   * Set the contents of the script.
   *
   * @param s the script
   */
  public void setScriptFilename(String s) {
    scriptFilename = Args.notBlank(s, "scriptFilename");
  }

  @Override
  protected void initService() throws CoreException {
    try {
      Args.notBlank(getScriptFilename(), "scriptFilename");
      File f = new File(getScriptFilename());
      FsWorker.isFile(FsWorker.checkReadable(f));
      super.initService();
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
  }

  @Override
  protected Reader createReader() throws IOException {
    return new FileReader(getScriptFilename());
  }
}
