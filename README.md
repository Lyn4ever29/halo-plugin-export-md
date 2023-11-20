# halo-plugin-export-md

Halo 2.0 插件——文章导入导出插件

## 更新日志
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
  -java
  -springcloud
tags:
  -springcloud
  -nacos
  -注册中心
---
```
- v1.0.0
  - 简单导出功能

## 安装
- 下载Release版本，直接安装即可

## 说明
- 与插件[ToolBench](https://www.halo.run/store/apps/app-SsYlH)一起使用时存在不兼容的问题，导致文章无法查看，后续修复。

## Todo
- [x] 导出为Markdown、HTML文件
- [x] 导出的Markdown文件包含属性
- [x] Markdown文章导入
- [ ] 导入Markdown时支持属性自定义
- [ ] 根据筛选条件（日期、分类、标签等）导出
- [ ] 导出适配其他平台的目录格式，如VuePress、Hexo等
- [ ] 从其他平台导入博客（待定）