# GlyphMatrix SDK

## Подключение

AAR библиотека находится в `libs/glyph-matrix-sdk-1.0.aar`

## Настройка AndroidManifest

Добавить permission:
```xml
<uses-permission android:name="com.nothing.ketchum.permission.ENABLE" />
```

## Основные компоненты

- **GlyphMatrixManager** - управление подключением и отображением
- **GlyphMatrixFrame** - рендеринг матрицы со слоями
- **GlyphMatrixObject** - элементы для отображения на матрице

## API

### Инициализация
```kotlin
GlyphMatrixManager.init(callback)
register(target)
```

### Управление
```kotlin
setMatrixFrame() // обновление дисплея
unInit() // отвязка от сервиса
```

## Взаимодействие

- Короткое нажатие: переключение между toys
- Длинное нажатие: кастомные действия
- Touch events для специфичной обработки

## Утилиты

### Добавление паддингов к GIF

Для обложки toy используйте ImageMagick (без потери качества):
```bash
magick input.gif -bordercolor transparent -border 5x5 output.gif
magick input.gif -bordercolor transparent -border 10x20 output.gif  # разные паддинги
```

Альтернативно - Python скрипт `add_padding_to_gif.py` (может снизить качество)

## Источник

https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit
