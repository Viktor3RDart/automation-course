FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy

WORKDIR /app

# Устанавливаем xvfb для запуска headed браузеров
RUN apt-get update && \
    apt-get install -y xvfb && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src ./src

RUN mvn dependency:go-offline -B

# Принудительно устанавливаем headless режим через системные свойства
CMD ["xvfb-run", "mvn", "test", "-Dtest=DynamicControlsTest", "-Dplaywright.headless=true", "-e"]