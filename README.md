# captureHead

captureHead 是 capture 的服务端，提供数据持久化和检索。

> capture 是 Web 端异常聚合服务，有 captureJS、captureView、captureHead 三个子项目构成。

项目地址：

- [captureJS](https://github.com/lazecoding/captureJS)
- [captureView](https://github.com/lazecoding/captureView)
- [captureHead](https://github.com/lazecoding/captureHead)

## 使用

### 日志分类

| 分类                 | 描述                  |
|--------------------|---------------------|
| js_error           | js 错误               |
| resource_error     | 资源引用错误              |
| vue_error          | Vue 错误              |
| promise_error      | promise 错误          |
| ajax_error         | ajax 异步请求错误         |
| console_info       | 控制台信息 console.info  |
| console_warn       | 控制台警告 console.warn  |
| console_error      | 控制台错误 console.error |
| cross_script_error | 跨域 js 错误            |
| unknow_error       | 未知异常                |
| performance        | 性能上报                |
| network_speed      | 网速上报                |

### 日志级别

| 级别     | 权重  | 使用范围 |
|--------|-----|------|
| ERROR  | 1   | 读写   |
| WARM   | 2   | 读写   |
| INFO   | 3   | 读写   |
| DEBUG  | 4   | 读写   |
| ALL    | 5   | 读    |

> 查询时会展现出该级别以及低于该级别权重的日志。

## License

Software is licenced under the Apache License Version 2.0. See the [LICENSE](https://github.com/lazecoding/captureHead/blob/main/LICENSE) file for details.