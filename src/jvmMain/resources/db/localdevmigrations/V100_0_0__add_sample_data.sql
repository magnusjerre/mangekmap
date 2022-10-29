INSERT INTO public.person(name, email, gender, retired) VALUES ('Donal Duck', 'donald.duck@mangekamp.no', 0, false);
INSERT INTO public.person(name, email, gender, retired) VALUES ('Dolly Duck', 'dolly.duck@mangekamp.no', 1, false);
INSERT INTO public.person(name, email, gender, retired) VALUES ('Onkel Skrue', 'onkel.skrue@mangekamp.no', 0, false);

INSERT INTO public.season(name, start_year) VALUES ('2022-2023 Oslo', 2022);

INSERT INTO public.event(date, title, venue, category_id, season_id, is_team_based) VALUES ('2022-08-25', 'Minigolf', 'Andeby minigolfpark', 2, 1, false);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(1, '40', 1, 1);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(1, '38', 1, 2);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(2, '43', 1, 3);

INSERT INTO public.event(date, title, venue, category_id, season_id, is_team_based) VALUES ('2022-09-06', 'Orientering', 'Andebyskogen', 1, 1, false);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(1, '12:00', 2, 1);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(1, '13:00', 2, 2);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(2, '14:00', 2, 3);

INSERT INTO public.event(date, title, venue, category_id, season_id, is_team_based) VALUES ('2022-09-06', 'Bueskyting', 'Andeby andejaktklubb', 1, 1, false);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(2, '30', 3, 1);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(1, '33', 3, 2);
INSERT INTO public.participant(rank, score, event_id, person_id) VALUES(1, '45', 3, 3);
