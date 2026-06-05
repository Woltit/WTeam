plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"

    /**
     * TodoPlugin — Gradle-плагін для автоматичного пошуку службових коментарів у коді.
     *
     * Що робить плагін:
     * Плагін сканує вихідний код проєкту під час збірки та знаходить коментарі на кшталт
     * TODO, FIXME та інші подібні позначки. Це дозволяє не губити важливі нотатки в коді,
     * а бачити їх у структурованому вигляді під час розробки або в CI.
     *
     * Навіщо він потрібен:
     * У великих або довгоживучих проєктах TODO-коментарі швидко накопичуються.
     * Частина з них означає незавершену роботу, частина — технічний борг, а частина просто
     * забувається. Цей плагін допомагає автоматично знаходити такі місця в коді,
     * щоб команда могла контролювати їх і не втрачати з поля зору.
     *
     * Що дає команді:
     * - робить незавершену роботу видимою;
     * - допомагає швидше знаходити технічний борг;
     * - спрощує рев’ю коду;
     * - може використовуватися локально або в CI;
     * - дає єдиний підхід до пошуку TODO/FIXME у всьому проєкті.
     *
     * Як користуватися:
     * 1. Підключи плагін у build.gradle.kts:
     *
     * plugins {
     *     id("com.lisovskyi.todo-plugin")
     * }
     *
     * 2. Налаштуй його через extension:
     *
     * todoPlugin {
     *     enabled.set(true)
     * }
     *
     * 3. Запусти Gradle task, який створює звіт або виконує сканування.
     *
     * Після цього плагін пройде по вихідних файлах і збере знайдені коментарі.
     *
     * Приклад використання:
     *
     * // TODO: винести логіку в окремий сервіс
     * // FIXME: прибрати hardcoded значення
     *
     * Плагін може знайти такі позначки і показати їх у результаті збірки.
     *
     * Для кого цей плагін:
     * - для команд, які хочуть тримати код чистішим;
     * - для backend-проєктів з активною розробкою;
     * - для CI-процесів, де важливо контролювати незавершені місця в коді.
     *
     * У майбутньому плагін можна розширити:
     * - додати список власних маркерів;
     * - додати виключення для окремих папок;
     * - генерувати текстовий або HTML-звіт;
     * - робити збірку fail, якщо знайдено TODO у критичних місцях.
     */
    id("com.wteam.todo-plugin")
}

group = "com.wteam"
version = "0.0.1"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

val jwtVersion              = "0.13.0"
val dotenvVersion           = "4.0.0"
val apacheCommonsVersion    = "3.20.0"

extra["snippetsDir"]        = file("build/generated-snippets")

dependencies {
    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")
    implementation("me.paulschwarz:spring-dotenv:$dotenvVersion")
    implementation("org.apache.commons:commons-lang3:$apacheCommonsVersion")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-mail-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restdocs")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-client-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}
