import com.yunfei.tinyworkflow.engine.*;
import org.junit.Assert;
import org.junit.Test;

public class TestWfEngine {
    private IWfEngine wfEngine = new WfEngine();
    @Test
    public void testRun() throws InterruptedException {
        wfEngine.init("workflow.xml");
        wfEngine.asyncRun((res)->{
            WfAsyncCallbackResult r = (WfAsyncCallbackResult) res;
            System.out.println(r);
        });
        Thread.sleep(200000);
    }
}
