# UML-диаграммы RoomBook

Диаграммы описывают целевую систему из SRS практической №2. Консольный прототип практической №3 реализует создание и подтверждение заявки, проверку пересечений и выгрузку списка в CSV.

## Варианты использования

```mermaid
flowchart LR
    User["Студент / преподаватель"]
    Dean["Сотрудник деканата"]
    Admin["Администратор"]
    SSO["Сервис SSO"]
    Mail["Почтовый сервис"]
    subgraph RoomBook
        Login(["Войти через SSO · FR-1"])
        Search(["Найти аудиторию · FR-2"])
        Request(["Создать заявку · FR-3, FR-4"])
        History(["Просмотреть свои заявки · FR-6"])
        Cancel(["Отменить бронь · FR-5"])
        Review(["Подтвердить или отклонить заявку · FR-8"])
        Rooms(["Управлять справочником · FR-9"])
        Report(["Выгрузить отчёт · FR-10"])
        Notify(["Уведомить о статусе · FR-7"])
    end
    User --- Login & Search & Request & History & Cancel
    Dean --- Login & Review & Report
    Admin --- Login & Rooms
    SSO --- Login
    Mail --- Notify
    Request -. include .-> Notify
    Review -. include .-> Notify
    Cancel -. include .-> Notify
```

## Диаграмма классов

```mermaid
classDiagram
    class User {
        +UUID id
        +String email
    }
    class Student
    class Teacher
    class DeanStaff
    class Administrator
    class Room {
        +String number
        +int capacity
        +Set~String~ equipment
        +boolean active
    }
    class Booking {
        +UUID id
        +String purpose
        +BookingStatus status
        +confirm()
        +reject(reason)
        +cancel()
    }
    class TimeSlot {
        +LocalDateTime start
        +LocalDateTime end
        +overlaps(other) boolean
    }
    class BookingService {
        +request(room, user, slot, purpose) Booking
        +confirm(id)
        +reject(id, reason)
        +cancel(id)
    }
    User <|-- Student
    User <|-- Teacher
    User <|-- DeanStaff
    User <|-- Administrator
    Booking "0..*" --> "1" User : заявитель
    Booking "0..*" --> "1" Room : аудитория
    Booking "1" *-- "1" TimeSlot : интервал
    BookingService ..> Booking : создаёт и обновляет
```

## Создание бронирования

```mermaid
sequenceDiagram
    actor U as Пользователь
    participant UI as Веб-интерфейс
    participant S as BookingService
    participant R as Хранилище
    participant N as Уведомления
    U->>UI: Выбрать аудиторию, время и цель
    UI->>S: Создать заявку
    S->>R: Проверить пересечение для аудитории
    R-->>S: Результат проверки
    alt Есть пересечение
        S-->>UI: Интервал занят
        UI-->>U: Предложить другое время
    else Интервал свободен
        S->>R: Сохранить заявку со статусом PENDING
        R-->>S: Возвратить идентификатор заявки
        S->>N: Опубликовать событие создания заявки
        S-->>UI: Вернуть идентификатор и статус
        UI-->>U: Показать «Ожидает подтверждения»
    end
```

В целевой системе проверка пересечения и запись выполняются атомарно. Уведомление инициируется после сохранения заявки.
