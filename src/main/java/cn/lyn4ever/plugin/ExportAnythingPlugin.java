package cn.lyn4ever.plugin;

import cn.lyn4ever.plugin.schema.ExportLogSchema;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
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

    public ExportAnythingPlugin(PluginWrapper wrapper, SchemeManager schemeManager) {
        super(wrapper);
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
        System.out.println("启动插件====");
    }

    @Override
    public void stop() {
        // 插件停用时取消注册自定义模型
        Scheme exportLog = schemeManager.get(ExportLogSchema.class);
        schemeManager.unregister(exportLog);
        System.out.println("插件停止====");
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