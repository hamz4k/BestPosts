object Versions {

    val kotlin = "1.3.30"
    val app_compat = "1.0.2"
    val core_ktx = "1.0.1"
    val junit = "4.12"
    val test_runner = "1.1.1"
    val esoresso_core = "3.1.1"

}

object Deps {
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val app_compat = "androidx.appcompat:appcompat:${Versions.app_compat}"
    val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    val junit = "junit:junit:${Versions.junit}"
    val test_runner = "androidx.test:runner:${Versions.test_runner}"
    val espresso_core = "androidx.test.espresso:espresso-core:${Versions.esoresso_core}"
}