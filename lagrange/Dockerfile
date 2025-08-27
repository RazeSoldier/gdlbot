# syntax=docker/dockerfile:1
# See: https://github.com/LagrangeDev/Lagrange.Core/blob/master/Docker.md
# 使用Lagrange镜像作为基础层，将预配置的appsettings.json复制到镜像中
# 配置定义了一个HTTP Websocks实现监听8089端口供GDLBot使用
FROM ghcr.io/lagrangedev/lagrange.onebot:edge

COPY appsettings.json /app/data/
COPY --chmod=645 check_health.sh /app/data/

RUN apt-get install --no-install-recommends -y curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*

HEALTHCHECK --interval=10s --timeout=3s CMD /app/data/check_health.sh

EXPOSE 8090