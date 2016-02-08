package com.hannesdorfmann.adaptercommands;

import android.support.v7.widget.RecyclerView;
import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class AdapterCommandProcessorTest {

  private AdapterCommandProcessor processor;
  private RecyclerView.Adapter adapter;

  @Before
  public void init(){
    adapter = Mockito.mock(RecyclerView.Adapter.class);
    this.processor = new AdapterCommandProcessor(adapter);
  }

  @Test public void nullpointerException() {
    try {
      new AdapterCommandProcessor(null);
      Assert.fail("Nullpointer expected");
    }catch (NullPointerException e){
      Assert.assertEquals("adapter == null", e.getMessage());
    }
  }

  @Test public void executeNullList(){
    try {
      processor.execute(null);
      Assert.fail("Nullpointer expected");
    } catch (NullPointerException e){
      Assert.assertEquals("commands == null", e.getMessage());
    }
  }

  @Test public void executeCommands(){

    AdapterCommand c1 = Mockito.mock(AdapterCommand.class);
    AdapterCommand c2 = Mockito.mock(AdapterCommand.class);

    List<AdapterCommand> commandList = new ArrayList<>();
    commandList.add(c1);
    commandList.add(c2);

    processor.execute(commandList);

    Mockito.verify(c1, Mockito.times(1)).execute(adapter);
    Mockito.verify(c2, Mockito.times(1)).execute(adapter);
  }


}