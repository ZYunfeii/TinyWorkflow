import com.yunfei.tinyworkflow.engine.*;
import com.yunfei.tinyworkflow.node.WfNode;
import com.yunfei.tinyworkflow.task.IWorkflowTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Slf4j
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
    @Slf4j
    public static class TestTask implements IWorkflowTask {
        private Integer count = 0;
        @Override
        public void run(WfNode node, WfContext ctx) throws Exception {
            log.info("Test Class run.");

            Thread.sleep(1000);
            if (count < 3) {
                count++;
                throw new RuntimeException("Test runtime exception.");
            }

            ctx.getResult().put(node.getId(), "approve");
            log.info("Test Class completed.");
        }
    }

    @Test
    public void testRetry() throws Exception {

//        Constructor<?> constructor = TestTask.class.getDeclaredConstructor();  // 获取无参构造函数
//
//        TestTask myInstance = (TestTask) constructor.newInstance();  // 实例化对象
//        System.out.println(myInstance);

        wfEngine.initWithWorkflowConfigFile("workflow.xml");
        wfEngine.changeTaskNodeCallback("task1", TestTask.class);
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

