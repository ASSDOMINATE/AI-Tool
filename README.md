#AI工具

### 接入ChatGPT


*关键代码位置*

> cn.hoxinte.ai.common.helper

* 可实现连续会话，采用Stream传输数据，即时性高
* Tokens计算会有误差200以内
* 改写openai源码，实现代理访问