import com.yunfei.tinyworkflow.engine.IWfEngine;
import com.yunfei.tinyworkflow.engine.WfEngine;
import org.junit.Test;

public class TestWfEngine {
    private IWfEngine wfEngine = new WfEngine();
    @Test
    public void testRun() {
        wfEngine.init("workflow.xml");
        wfEngine.run();
    }
}
