package com.adaptris.core.scripting.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.adaptris.core.util.Args;

public final class ScriptEngineUtils {

  public static final String JAVASCRIPT = "javascript";

  private ScriptEngineUtils() {
  }

  public static void nashornCompat() {
    System.setProperty("polyglot.js.nashorn-compat", "true");
  }

  public static ScriptEngine getEngineByName(String language, ClassLoader classLoader) {
    ScriptEngineManager engineManager = new ScriptEngineManager(classLoader);
    String error = String.format("getEngineByName(\'%s\')", language);
    return Args.notNull(engineManager.getEngineByName(language), error);
  }

}
