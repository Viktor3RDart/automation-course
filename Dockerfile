FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy

WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Устанавливаем Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Устанавливаем зависимости
RUN mvn dependency:go-offline -B

# Команда по умолчанию
CMD ["xvfb-run", "mvn", "test", "-Dtest=CheckboxTest", "-Dplaywright.headless=true", "-e"]