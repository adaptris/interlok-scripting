package com.adaptris.core.scripting.services;

import java.io.IOException;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.BooleanUtils;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.BranchingServiceCollection;
import com.adaptris.core.CoreException;
import com.adaptris.core.DynamicPollingTemplate;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.scripting.utils.ScriptEngineUtils;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Base class for enabling JSR223 enabled scripting languages.
 *
 *
 * @author lchan
 */
public abstract class ScriptingServiceImp extends ServiceImp implements DynamicPollingTemplate.TemplateProvider {

  /**
   * Set the JSR223 language the the script is written in.
   * <p>
   * Depending on the language choice, you may need additional libraries that are not normally shipped with a standard Interlok
   * distribution.
   * </p>
   */
  @NotBlank
  @NonNull
  @Getter
  @Setter
  private String language;
  private transient ScriptEngine engine;

  /**
   * Specify whether or not this service is branching so it can be used as part of a {@link BranchingServiceCollection}.
   */
  @InputFieldDefault("false")
  @AdvancedConfig
  @Getter
  @Setter
  private Boolean branchingEnabled;

  static {
    ScriptEngineUtils.nashornCompat();
  }

  public ScriptingServiceImp() {
    super();
  }

  @Override
  public final void doService(AdaptrisMessage msg) throws ServiceException {
    try {
      Bindings vars = engine.createBindings();
      vars.put("message", msg);
      vars.put("log", log);
      try (Reader input = createReader()) {
        engine.eval(input, vars);
      }
    } catch (Exception e) {
      throw ExceptionHelper.wrapServiceException(e);
    }
  }

  protected abstract Reader createReader() throws IOException;

  @Override
  protected void initService() throws CoreException {
  }

  @Override
  protected void closeService() {
  }

  @Override
  public void prepare() throws CoreException {
    try {
      Args.notBlank(getLanguage(), "language");
      engine = ScriptEngineUtils.getEngineByName(getLanguage(), this.getClass().getClassLoader());
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
  }

  @Override
  public boolean isBranching() {
    return BooleanUtils.toBooleanDefaultIfNull(getBranchingEnabled(), false);
  }

}
