import com.yunfei.tinyworkflow.engine.*;
import org.junit.Assert;
import org.junit.Test;

public class TestWfEngine {
    private IWfEngine wfEngine = new WfEngine();
//    @Test
//    public void testRun() throws InterruptedException {
//        wfEngine.initWithWorkflowConfigFile("workflow.xml");
//        wfEngine.asyncRun((res)->{
//            WfAsyncCallbackResult r = (WfAsyncCallbackResult) res;
//            System.out.println(r);
//        });
//        Thread.sleep(200000);
//    }

    @Test
    public void testStopThenRunAgain() throws InterruptedException {
        wfEngine.initWithWorkflowConfigFile("workflow.xml");
        wfEngine.asyncRun((res)->{
            WfAsyncCallbackResult r = (WfAsyncCallbackResult) res;
            System.out.println(r);
        });

        Thread.sleep(2000);
        wfEngine.stop();

        Thread.sleep(5000);
        wfEngine.syncRun();

    }

    @Test
    public void testStopAndInitThenRun() throws InterruptedException {
        wfEngine.initWithWorkflowConfigFile("workflow.xml");
        wfEngine.asyncRun((res)->{
            WfAsyncCallbackResult r = (WfAsyncCallbackResult) res;
            System.out.println(r);
        });

        Thread.sleep(2000);
        wfEngine.stop();
        wfEngine.init();
        wfEngine.syncRun();
    }

    @Test
    public void testOther() {
        String javaClassPath = System.getProperty("java.class.path");
        System.out.println(javaClassPath);
        Integer a;
        a = 2;
        System.out.println(a);
    }
}
