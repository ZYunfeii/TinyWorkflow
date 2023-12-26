import com.yunfei.tinyworkflow.engine.IWfEngine;
import com.yunfei.tinyworkflow.engine.WfAsyncCallbackResult;
import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.engine.WfEngine;
import org.junit.Assert;
import org.junit.Test;

public class TestWfEngine {
    private IWfEngine wfEngine = new WfEngine();
    @Test
    public void testRun() throws InterruptedException {
        wfEngine.init("workflow.xml");
        wfEngine.asyncRun((res)->{
            System.out.println(res);
        });
        Thread.sleep(200000);
    }
}
