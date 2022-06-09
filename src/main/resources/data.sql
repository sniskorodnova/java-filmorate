MERGE INTO rating_mpaa (RATING_MPAA_ID, NAME) VALUES (1, 'G');
MERGE INTO rating_mpaa (RATING_MPAA_ID, NAME) VALUES (2, 'PG');
MERGE INTO rating_mpaa (RATING_MPAA_ID, NAME) VALUES (3, 'PG-13');
MERGE INTO rating_mpaa (RATING_MPAA_ID, NAME) VALUES (4, 'R');
MERGE INTO rating_mpaa (RATING_MPAA_ID, NAME) VALUES (5, 'NC-17');

MERGE INTO genre (GENRE_ID, NAME) VALUES (1, 'Комедия');
MERGE INTO genre (GENRE_ID, NAME) VALUES (2, 'Драма');
MERGE INTO genre (GENRE_ID, NAME) VALUES (3, 'Мультфильм');
MERGE INTO genre (GENRE_ID, NAME) VALUES (4, 'Триллер');
MERGE INTO genre (GENRE_ID, NAME) VALUES (5, 'Документальное');
MERGE INTO genre (GENRE_ID, NAME) VALUES (6, 'Боевик');
