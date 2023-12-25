import com.yunfei.tinyworkflow.loader.EngineConfigLoader;
import com.yunfei.tinyworkflow.loader.IEngineConfigLoader;
import com.yunfei.tinyworkflow.loader.WorkflowConfigLoader;
import com.yunfei.tinyworkflow.loader.IWorkflowConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestConfigLoader {
    @Test
    public void testLoadConfig() throws Exception {
        log.info("start test");
        IWorkflowConfigLoader configLoader = new WorkflowConfigLoader();
        configLoader.loadConfig("workflow.xml");
    }

    @Test
    public void testLoadEngineConfig() {
        IEngineConfigLoader configLoader = new EngineConfigLoader();
        configLoader.loadConfig("config.yaml");
    }
}
