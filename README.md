Halo 2.0 插件——文章导入导出插件

## 功能说明
- 快速导出文章为markdown或者html文件
- 导出后打包下载
- 提供文章导入功能，支持导入markdown文件

## 预览
- 文章列表导出功能

![https://github.com/Lyn4ever29/halo-plugin-export-md/assets/25952589/dd247afe-76f0-402f-9f3c-68b265d7b8ab](https://github.com/Lyn4ever29/halo-plugin-export-md/assets/25952589/dd247afe-76f0-402f-9f3c-68b265d7b8ab)

- 导出列表

![导出列表](https://github.com/Lyn4ever29/halo-plugin-export-md/assets/25952589/2404ae3c-582b-4f5e-b9b6-96f7b029af69)


## 安装
- 下载[Release](https://github.com/Lyn4ever29/halo-plugin-export-md/releases)版本，直接安装即可
- 在Halo应用市场，支持一键安装[文章导入导出](https://www.halo.run/store/apps/app-vWbpZ)

## 说明
- 与插件[ToolBench](https://www.halo.run/store/apps/app-SsYlH)一起使用时存在不兼容的问题，导致文章无法查看。
- 如果您有任何问题或者好的建议，请提issue给我。


## 更新日志
- v1.2.6
  - 适配Halo2.21版本，感谢[Zhangfen21082](https://github.com/Zhangfen21082)网友的修复
  - 修改markdown4j库为[flexmark-java](https://github.com/vsch/flexmark-java)
  - 修改插件编写方式为最新版本，将console目录同步修改为ui目录


- v1.2.5
  - 修复导出文章时，出现的错误问题

- v1.2.4 
  - 将导入导出页面的入口放置在工具菜单项下作为子菜单项（2.12版本的特性）。
  - 需要注意，此改动需要 2.12 的支持
- v1.2.3 优化导入导出的 UI。（来自[@ruibaby](https://github.com/ruibaby)的PR）
  - 支持显示空状态，引导用户导出文章。
  - 支持导出/删除记录之后自动更新列表数据，不再需要手动刷新页面。
  - 使用 Halo 在全局注册的 Uppy 组件，减少构建产物体积。（已同步修改 plugin.yaml 的 requires 为 2.11）
  - 使用 Halo 官方新提供的 @halo-dev/ui-plugin-bundler-kit 用于构建插件。
  - 移除部分无用样式和注释。
- v1.2.2
  - 导出文章时，添加封面图字段
  - 添加权限控制，感谢[@chengzhongxue](https://github.com/chengzhongxue)的PR
- v1.2.1
  - 修复halo2.11版本升级后，导入功能失效的问题（由于Halo修改了文章发布机制，导致导入失效，已更新至最新代码）。
  - 如果是halo2.10版本，请使用**v1.1.4**
- v1.2.0
  - 更改导出文章压缩包目录为halo2工作目录
  - 修改有关草稿箱的说明，导入后的文章处于待发布状态，需要用户自行发布
- v1.1.4 在文章列表添加导出快捷方式，可以导出单文件。
- v1.1.0 
  - 支持导入Markdown文件
  - 导出的Mardkdown文件支持属性，属性示例如下：
  
  ```yaml
  ---
  title: 试试Nacos作注册中心和配置中心，爱不释手的感觉
  date: 2023-04-22 20:28:05
  auther: lyn4ever
  excerpt: 在使用SpringCloud做分布式微服务架构时，注册中心是必不可少的一个组件。
  permalink: /2022/166359134426
  categories:
    - java
    - springcloud
  tags:
    - springcloud
    - nacos
    - 注册中心
  ---
  ```
- v1.0.0
  - 简单导出功能

## Todo
- [x] 导出为Markdown、HTML文件
- [x] 导出的Markdown文件包含属性
- [x] Markdown文章导入
- [ ] 导入Markdown时支持属性自定义
- [ ] 根据筛选条件（日期、分类、标签等）导出
- [ ] 导出适配其他平台的目录格式，如VuePress、Hexo等
- [ ] 从其他平台导入博客（待定）
