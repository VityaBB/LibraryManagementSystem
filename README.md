# Library Management System


Ссылка на SonarCloud: https://sonarcloud.io/project/overview?id=VityaBB_LibraryManagementSystem



## План

| Задача      | P | O | BG | **Запланированное время** | **Фактическое время** |
|:------------|:--|:--|:---|:------------------------|:---------------------------------|
| БД          | 8 | 2 | 4  | 4.3                    | 3                              |
| JDBC        | 10 | 5 | 8 | 7.8                     | 9                              |
| Backend     | 12 | 6 | 8  | 8.3                       | 6.5                                |
| React       | 15 | 7 | 9  | 9.7                       | 12.5                                |
| Angular     | 12 | 6 | 8  | 8.3                       | 7                                |
| Docker      | 7 | 2 | 5  | 4.8                       | 4.5                                |


Оценка времени: 
- Пессимистичная (P)
- Оптимистичная (O)
- Наиболее вероятная (BG) 

**E = (P + O + 4 * BG) / 6 **

## Запуск через Docker

1. Создай файл секретов (один раз):
```bash
    copy .env.example .env
```
2. Задай в пароль БД и другие переменные `.env`
3. Подними проект 
```bash
    docker-compose up -d --build
```
4. Angular:	http://localhost:4200