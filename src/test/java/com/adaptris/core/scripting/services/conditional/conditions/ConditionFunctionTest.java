package com.adaptris.core.scripting.services.conditional.conditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.util.LifecycleHelper;

public class ConditionFunctionTest {

  @Test
  public void testDefinition() throws Exception {
    ConditionFunction condition = new ConditionFunction();
    assertNull(condition.getDefinition());
    condition.setDefinition("function evaluateScript(message) { return true;}");
    assertEquals("function evaluateScript(message) { return true;}", condition.getDefinition());
  }

  @Test
  public void testScriptCondition_True() throws Exception {
    ConditionFunction condition = new ConditionFunction("function evaluateScript(message) { return true;}");
    try {
      LifecycleHelper.initAndStart(condition);
      assertTrue(condition.evaluate(createMessage()));
    } finally {
      LifecycleHelper.stopAndClose(condition);
    }
  }

  @Test
  public void testScriptCondition_False() throws Exception {
    ConditionFunction condition = new ConditionFunction("function evaluateScript(message) { return false; }");
    try {
      LifecycleHelper.initAndStart(condition);
      assertFalse(condition.evaluate(createMessage()));
    } finally {
      LifecycleHelper.stopAndClose(condition);
    }
  }

  @Test
  public void testScriptConditionAgainstMetadata() throws Exception {
    ConditionFunction condition = new ConditionFunction(
        "function evaluateScript(message) { return message.getMetadataValue('key').equals('value');}");
    try {
      LifecycleHelper.initAndStart(condition);
      assertTrue(condition.evaluate(createMessage()));
      assertFalse(condition.evaluate(createMessage("key", "anotherValue")));
    } finally {
      LifecycleHelper.stopAndClose(condition);
    }
  }

  @Test
  public void testScriptCondition_BrokenFunction() throws Exception {
    Assertions.assertThrows(CoreException.class, () -> {
      ConditionFunction condition = new ConditionFunction("function evaluateScript(message) }");
      LifecycleHelper.initAndStart(condition);
    });
  }

  @Test
  public void testScriptCondition_WrongFunction() throws Exception {
    Assertions.assertThrows(CoreException.class, () -> {
      ConditionFunction condition = new ConditionFunction("function wrongFunctionName(message) {return true;}");
      try {
        LifecycleHelper.initAndStart(condition);
        condition.evaluate(createMessage());
      } finally {
        LifecycleHelper.stopAndClose(condition);
      }
    });
  }

  private AdaptrisMessage createMessage() {
    return createMessage("key", "value");
  }

  private AdaptrisMessage createMessage(String key, String value) {
    AdaptrisMessage msg = new DefaultMessageFactory().newMessage();
    msg.addMessageHeader(key, value);
    return msg;
  }

}

