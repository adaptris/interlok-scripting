package com.adaptris.core.scripting.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.interlok.junit.scaffolding.services.ExampleServiceCase;

public class ScriptingServiceTest extends ExampleServiceCase {

  private static final String KEY_SCRIPTING_BASEDIR = "scripting.basedir";
  public static final String SCRIPT = "\nvalue = message.getMetadataValue('MyMetadataKey');"
      + "\nmessage.addMetadata('MyMetadataKey', value.split(\"\").reverse().join(\"\"));";

  private static final String MY_METADATA_VALUE = "MyMetadataValue";
  private static final String MY_METADATA_KEY = "MyMetadataKey";

  private File writeScript(boolean working) throws IOException {
    File result = null;
    File dir = new File(PROPERTIES.getProperty(KEY_SCRIPTING_BASEDIR, "./build/scripting"));
    dir.mkdirs();
    result = File.createTempFile("junit", ".script", dir);
    try (FileWriter fw = new FileWriter(result)) {
      fw.write(working ? SCRIPT : "This will fail");
    }
    return result;
  }

  private void delete(File script) throws Exception {
    if (script == null) {
      return;
    }
    if (!script.delete()) {
      throw new Exception("failed to delete file");
    }
  }

  @Test
  public void testService() throws Exception {
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    msg.addMetadata(MY_METADATA_KEY, MY_METADATA_VALUE);
    ScriptingService service = createService();
    File script = writeScript(true);
    service.setScriptFilename(script.getCanonicalPath());
    execute(service, msg);
    assertTrue(msg.headersContainsKey(MY_METADATA_KEY));
    assertNotSame(MY_METADATA_VALUE, msg.getMetadataValue(MY_METADATA_KEY));
    assertEquals(new StringBuffer(MY_METADATA_VALUE).reverse().toString(), msg.getMetadataValue(MY_METADATA_KEY));
    delete(script);
  }

  @Test
  public void testInit() throws Exception {
    ScriptingService service = new ScriptingService();

    Assertions.assertThrows(Exception.class, () -> {
      service.init();
    });

    service.setLanguage("javascript");
    service.setScriptFilename("/BLAHBLAHBLAHBLAHBLAH/BLAHBLAHBLAHBLAH");
    Assertions.assertThrows(Exception.class, () -> {
      service.init();
    });

    File script = writeScript(false);
    service.setScriptFilename(script.getCanonicalPath());
    LifecycleHelper.init(service);
    LifecycleHelper.close(service);
    delete(script);
  }

  @Test
  public void testInitWithJsLanguage() throws Exception {
    ScriptingService service = new ScriptingService();
    File script = writeScript(false);
    service.setScriptFilename(script.getCanonicalPath());
    service.setLanguage("javascript");

    LifecycleHelper.init(service);
    LifecycleHelper.close(service);

    service.setLanguage("js");

    LifecycleHelper.init(service);
    LifecycleHelper.close(service);

    service.setLanguage("ecmascript");

    LifecycleHelper.init(service);
    LifecycleHelper.close(service);

    delete(script);
  }

  @Test
  public void testDoServiceWithFailingScript() throws Exception {
    ScriptingService service = createService();
    File script = writeScript(false);
    service.setScriptFilename(script.getCanonicalPath());
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    Assertions.assertThrows(Exception.class, () -> {
      execute(service, msg);
    });
    delete(script);
  }

  @Override
  protected Object retrieveObjectForSampleConfig() {
    ScriptingService service = createService();
    service.setScriptFilename("/path/to/script/you/want/to/execute");
    return service;
  }

  private ScriptingService createService() {
    ScriptingService result = new ScriptingService("scripting-service");
    result.setLanguage("javascript");
    return result;
  }

  @Override
  protected String getExampleCommentHeader(Object o) {
    return super.getExampleCommentHeader(o) + "<!--"
        + "\nThis allows to embed scripts written in any language that supports JSR223 (e.g. jruby)."
        + "\nThe script is executed and the AdaptrisMessage that is due to be processed is"
        + "\nbound against the key 'message' and an instance of org.slf4j.Logger is also bound "
        + "\nto key 'log'. These can be used as a standard variable within the script." + "\n-->\n";
  }

}
