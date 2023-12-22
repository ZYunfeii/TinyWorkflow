import com.yunfei.tinyworkflow.loader.ConfigLoader;
import com.yunfei.tinyworkflow.loader.IConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
@Slf4j
public class TestConfigLoader {
    @Test
    public void testLoadConfig() throws Exception {
        log.info("start test");
        IConfigLoader configLoader = new ConfigLoader();
        configLoader.loadConfig("workflow.xml");
    }
}
