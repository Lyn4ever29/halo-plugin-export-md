package cn.lyn4ever.export2md;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import run.halo.app.plugin.PluginContext;

@ExtendWith(MockitoExtension.class)
class HaloPluginExport2docPluginTest {

    @Mock
    PluginContext context;

    @InjectMocks
    HaloPluginExport2docPlugin plugin;
    
    @Test
    void contextLoads() {
        // plugin.start();
        // plugin.stop();
    }
}
