package cn.lyn4ever.export2md;

import cn.lyn4ever.export2md.schema.ExportLogSchema;
import cn.lyn4ever.export2md.schema.ImportLogSchema;
import org.pf4j.PluginManager;
import run.halo.app.plugin.PluginContext;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/1
 */
@Component
public class ExportAnythingPlugin extends BasePlugin {
    private final SchemeManager schemeManager;
    
    public ExportAnythingPlugin(PluginContext context, SchemeManager schemeManager) {
        super(context);
        this.schemeManager = schemeManager;
    }

    /**
     * This method is called by the application when the plugin is started.
     * See {@link PluginManager#startPlugin(String)}.
     */
    @Override
    public void start() {
        // 插件启动时注册自定义模型
        schemeManager.register(ExportLogSchema.class);
        schemeManager.register(ImportLogSchema.class);
    }

    @Override
    public void stop() {
        // 插件停用时取消注册自定义模型
        schemeManager.unregister(schemeManager.get(ExportLogSchema.class));
        schemeManager.unregister(schemeManager.get(ImportLogSchema.class));

    }

    /**
     * This method is called by the application when the plugin is deleted.
     * See {@link PluginManager#deletePlugin(String)}.
     */
    @Override
    public void delete() {
        super.delete();
    }
}