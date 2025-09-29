# # Базовый образ с Playwright и браузерами
# FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy
#
# # Установка зависимостей проекта
# WORKDIR /app
# COPY pom.xml ./
# COPY src ./src
# RUN apt-get update && apt-get install -y maven && mvn clean install

# Базовый образ с Playwright, Java и Maven
FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Обновляем Maven до последней версии
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Устанавливаем зависимости Maven
RUN mvn dependency:go-offline -B

# Устанавливаем браузеры Playwright
# RUN playwright install --with-deps

# Создаем non-root пользователя для безопасности
RUN useradd -m playwrightuser
USER playwrightuser

# Команда по умолчанию (может быть переопределена в docker run)
CMD ["mvn", "test", "-Dtest=statusCodeApiUiTest"]
