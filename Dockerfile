FROM mcr.microsoft.com/playwright/java:v1.50.0-jammy

WORKDIR /app

# Копируем только необходимые файлы в правильном порядке
COPY pom.xml .
COPY src ./src

# Пропускаем фазу ресурсов при установке зависимостей
RUN mvn dependency:go-offline -B -DskipTests -Dmaven.main.skip=true -Dmaven.resources.skip=true

# Команда для запуска тестов (пропускаем компиляцию ресурсов)
CMD ["mvn", "test", "-Dtest=statusCodeApiUiTest", "-Dmaven.resources.skip=true", "-e"]