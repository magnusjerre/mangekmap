INSERT INTO public.person(name, email, gender, retired) VALUES ('Donald Duck', 'donald.duck@mangekamp.no', 0, false);
INSERT INTO public.person(name, email, gender, retired) VALUES ('Dolly Duck', 'dolly.duck@mangekamp.no', 1, false);
INSERT INTO public.person(name, email, gender, retired) VALUES ('Onkel Skrue', 'onkel.skrue@mangekamp.no', 0, false);

INSERT INTO public.season(name, start_year, mangekjemper_required_events, region) VALUES ('2022-2023 Oslo', 2022, 8, 0);

INSERT INTO public.event(date, title, venue, category_id, season_id, is_team_based) VALUES ('2022-08-25', 'Minigolf', 'Andeby minigolfpark', 2, 1, false);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(1, '40', false, 1, 1);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(1, '38', false, 1, 2);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(2, '43', false, 1, 3);

INSERT INTO public.event(date, title, venue, category_id, season_id, is_team_based) VALUES ('2022-09-06', 'Orientering', 'Andebyskogen', 1, 1, false);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(1, '12:00', false, 2, 1);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(1, '13:00', false, 2, 2);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(2, '14:00', false, 2, 3);

INSERT INTO public.event(date, title, venue, category_id, season_id, is_team_based) VALUES ('2022-09-06', 'Bueskyting', 'Andeby andejaktklubb', 1, 1, false);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(2, '30', false, 3, 1);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(1, '33', false, 3, 2);
INSERT INTO public.participant(rank, score, is_attendance_only, event_id, person_id) VALUES(1, '45', false, 3, 3);
