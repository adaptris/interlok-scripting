package com.adaptris.core.scripting.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;

import com.adaptris.core.util.Args;

public final class ScriptEngineUtilsTest {

  public static ScriptEngine getEngineByName(String language, ClassLoader classLoader) {
    ScriptEngineManager engineManager = new ScriptEngineManager(classLoader);
    String error = String.format("getEngineByName(\'%s\')", language);
    return Args.notNull(engineManager.getEngineByName(language), error);
  }

  @Test
  public void testGetEngineByNameJavascript() {
    ScriptEngine engine = ScriptEngineUtils.getEngineByName("javascript", this.getClass().getClassLoader());

    assertNotNull(engine);
  }

  @Test
  public void testGetEngineByNameBeanshell() {
    ScriptEngine engine = ScriptEngineUtils.getEngineByName("beanshell", this.getClass().getClassLoader());

    assertNotNull(engine);
  }

  @Test
  public void testGetEngineByNameDoesntExist() {
    assertThrows(IllegalArgumentException.class, () -> ScriptEngineUtils.getEngineByName("doesntexist", this.getClass().getClassLoader()));
  }

}
