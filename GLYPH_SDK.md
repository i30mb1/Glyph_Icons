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

## Источник

https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit
