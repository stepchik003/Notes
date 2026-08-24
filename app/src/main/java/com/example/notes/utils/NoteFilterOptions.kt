package com.example.notes.utils

enum class SortOrder(val displayName: String) {
    UPDATED_AT_DESC("Сначала новые (по дате изменения)"),
    UPDATED_AT_ASC("Сначала старые (по дате изменения)"),
    CREATED_AT_DESC("Сначала новые (по дате создания)"),
    CREATED_AT_ASC("Сначала старые (по дате создания)"),
    CONTENT_LENGTH_DESC("Сначала длинные"),
    CONTENT_LENGTH_ASC("Сначала короткие"),
    TITLE_ASC("А–Я по названию"),
    TITLE_DESC("Я–А по названию")
}

enum class ImageFilter(val displayName: String) {
    ALL("Все"),
    ONLY_WITH_IMAGES("Только с фото"),
    WITHOUT_IMAGES("Без фото")
}

enum class DraftFilter(val displayName: String) {
    ALL("Все"),
    ONLY_DRAFTS("Только черновики"),
    WITHOUT_DRAFTS("Без черновиков")
}