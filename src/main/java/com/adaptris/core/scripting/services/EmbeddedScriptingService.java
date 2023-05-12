package com.adaptris.core.scripting.services;

import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.MarshallingCDATA;
import com.adaptris.core.BranchingServiceCollection;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Getter;
import lombok.Setter;

/**
 * Supports arbitary scripting languges that are supported by JSR223.
 * <p>
 * You should take care when configuring this class; it can present an audit trail issue when used in combination with
 *
 * {@link com.adaptris.core.services.dynamic.DynamicServiceExecutor} if your script executes arbitrary system commands. In that situation,
 * all commands will be executed with the current users permissions and may be subject to the virtual machines security manager.
 * </p>
 * <p>
 * The script is executed and the AdaptrisMessage that is due to be processed is bound against the key "message" and an instance of
 * org.slf4j.Logger is also bound to key "log". These can be used as a standard variable within the script.
 * </p>
 * <p>
 * Note that this class can be used as the selector as part of a {@link BranchingServiceCollection}. If used as such, then you need to
 * remember to invoke {@link com.adaptris.core.AdaptrisMessage#setNextServiceId(String)} as part of the script and
 * {@link #setBranchingEnabled(Boolean)} should be true.
 * <p>
 *
 * @config embedded-scripting-service
 *
 * @author lchan
 *
 */
@XStreamAlias("embedded-scripting-service")
@AdapterComponent
@ComponentProfile(summary = "Execute an embedded JSR223 script", tag = "service,scripting", branchSelector = true)
@DisplayOrder(order = { "language", "script", "branchingEnabled" })
public class EmbeddedScriptingService extends ScriptingServiceImp {

  /**
   * Set the contents of the script.
   *
   * @param script
   *          the script
   */
  @MarshallingCDATA
  @Getter
  @Setter
  private String script;

  public EmbeddedScriptingService() {
    super();
  }

  public EmbeddedScriptingService(String uniqueId) {
    this();
    setUniqueId(uniqueId);
  }

  @Override
  protected Reader createReader() {
    return new StringReader(StringUtils.defaultIfEmpty(getScript(), ""));
  }

  public EmbeddedScriptingService withScript(String lang, String script) {
    setScript(script);
    setLanguage(lang);
    return this;
  }

}
