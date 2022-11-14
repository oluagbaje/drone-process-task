--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5
-- Dumped by pg_dump version 14.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: drone_battery_battery_charge_log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.drone_battery_battery_charge_log (
    hist_item_id integer NOT NULL,
    drone_serial_number character(100) NOT NULL,
    drone_batterylevel_before_charge double precision NOT NULL,
    drone_batterylevel_after_charge double precision NOT NULL,
    charge_start_time text NOT NULL,
    charge_end_time text NOT NULL,
    charge_duration integer NOT NULL,
    drone_state text NOT NULL
);


ALTER TABLE public.drone_battery_battery_charge_log OWNER TO postgres;

--
-- Name: drone_battery_battery_charge_log_hist_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.drone_battery_battery_charge_log_hist_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.drone_battery_battery_charge_log_hist_item_id_seq OWNER TO postgres;

--
-- Name: drone_battery_battery_charge_log_hist_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.drone_battery_battery_charge_log_hist_item_id_seq OWNED BY public.drone_battery_battery_charge_log.hist_item_id;


--
-- Name: drone_process_hist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.drone_process_hist (
    hist_item_id integer NOT NULL,
    drone_serial_number character(100) NOT NULL,
    drone_model character(15) NOT NULL,
    drone_weight_limit double precision NOT NULL,
    drone_battery_level double precision NOT NULL,
    drone_state text NOT NULL,
    med_code character(16) NOT NULL,
    med_name character(16) NOT NULL,
    med_weight double precision NOT NULL,
    med_image text NOT NULL,
    process_start_time text NOT NULL,
    process_end_time text NOT NULL
);


ALTER TABLE public.drone_process_hist OWNER TO postgres;

--
-- Name: drone_process_hist_hist_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.drone_process_hist_hist_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.drone_process_hist_hist_item_id_seq OWNER TO postgres;

--
-- Name: drone_process_hist_hist_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.drone_process_hist_hist_item_id_seq OWNED BY public.drone_process_hist.hist_item_id;


--
-- Name: drone_process_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.drone_process_record (
    drone_serial_number character(100) NOT NULL,
    drone_model character(15) NOT NULL,
    drone_weight_limit double precision NOT NULL,
    drone_battery_level double precision NOT NULL,
    drone_state text NOT NULL
);


ALTER TABLE public.drone_process_record OWNER TO postgres;

--
-- Name: medication_tbl; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.medication_tbl (
    med_code character(16) NOT NULL,
    med_name character(16) NOT NULL,
    med_weight double precision NOT NULL,
    med_image text NOT NULL
);


ALTER TABLE public.medication_tbl OWNER TO postgres;

--
-- Name: drone_battery_battery_charge_log hist_item_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.drone_battery_battery_charge_log ALTER COLUMN hist_item_id SET DEFAULT nextval('public.drone_battery_battery_charge_log_hist_item_id_seq'::regclass);


--
-- Name: drone_process_hist hist_item_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.drone_process_hist ALTER COLUMN hist_item_id SET DEFAULT nextval('public.drone_process_hist_hist_item_id_seq'::regclass);


--
-- Data for Name: drone_battery_battery_charge_log; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.drone_battery_battery_charge_log (hist_item_id, drone_serial_number, drone_batterylevel_before_charge, drone_batterylevel_after_charge, charge_start_time, charge_end_time, charge_duration, drone_state) FROM stdin;
1	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 16:56:52	0000-00-00 00:00:00	20	IDLE
2	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 16:57:12	2022-11-06 16:57:12	20	IDLE
3	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 16:57:12	0000-00-00 00:00:00	20	IDLE
4	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 16:57:32	2022-11-06 16:57:32	20	IDLE
5	AKT1004SMG                                                                                          	0.23	0.23	2022-11-06 16:57:32	0000-00-00 00:00:00	20	IDLE
6	AKT1004SMG                                                                                          	0.23	0.98	2022-11-06 16:57:52	2022-11-06 16:57:52	20	IDLE
7	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 16:57:57	0000-00-00 00:00:00	20	IDLE
8	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 16:58:17	2022-11-06 16:58:17	20	IDLE
9	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 16:58:17	0000-00-00 00:00:00	20	IDLE
10	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:00:36	0000-00-00 00:00:00	20	IDLE
11	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:00:56	2022-11-06 17:00:56	20	IDLE
12	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:00:56	0000-00-00 00:00:00	20	IDLE
13	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:01:16	2022-11-06 17:01:16	20	IDLE
14	AKT1004SMG                                                                                          	0.23	0.23	2022-11-06 17:01:16	0000-00-00 00:00:00	20	IDLE
15	AKT1004SMG                                                                                          	0.23	0.98	2022-11-06 17:01:36	2022-11-06 17:01:36	20	IDLE
16	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:01:41	0000-00-00 00:00:00	20	IDLE
17	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:02:01	2022-11-06 17:02:01	20	IDLE
18	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:02:01	0000-00-00 00:00:00	20	IDLE
19	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:02:21	2022-11-06 17:02:21	20	IDLE
20	AKT1004SMG                                                                                          	0.23	0.23	2022-11-06 17:02:21	0000-00-00 00:00:00	20	IDLE
21	AKT1004SMG                                                                                          	0.23	0.98	2022-11-06 17:02:41	2022-11-06 17:02:41	20	IDLE
22	AKT1005SMG                                                                                          	0.16	0.16	2022-11-06 17:02:46	0000-00-00 00:00:00	20	IDLE
23	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:04:55	0000-00-00 00:00:00	20	IDLE
24	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:05:16	2022-11-06 17:05:16	20	IDLE
25	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:05:16	0000-00-00 00:00:00	20	IDLE
26	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:05:36	2022-11-06 17:05:36	20	IDLE
27	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:05:41	0000-00-00 00:00:00	20	IDLE
28	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:06:01	2022-11-06 17:06:01	20	IDLE
29	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:06:01	0000-00-00 00:00:00	20	IDLE
30	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:06:21	2022-11-06 17:06:21	20	IDLE
31	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:06:26	0000-00-00 00:00:00	20	IDLE
32	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:06:46	2022-11-06 17:06:46	20	IDLE
33	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:06:46	0000-00-00 00:00:00	20	IDLE
34	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:07:06	2022-11-06 17:07:06	20	IDLE
35	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:07:11	0000-00-00 00:00:00	20	IDLE
36	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:07:31	2022-11-06 17:07:31	20	IDLE
37	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:07:31	0000-00-00 00:00:00	20	IDLE
38	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:07:51	2022-11-06 17:07:51	20	IDLE
39	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:07:56	0000-00-00 00:00:00	20	IDLE
40	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:08:16	2022-11-06 17:08:16	20	IDLE
41	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:08:16	0000-00-00 00:00:00	20	IDLE
42	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:08:36	2022-11-06 17:08:36	20	IDLE
43	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:08:41	0000-00-00 00:00:00	20	IDLE
44	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:09:01	2022-11-06 17:09:01	20	IDLE
45	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:09:01	0000-00-00 00:00:00	20	IDLE
46	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:09:21	2022-11-06 17:09:21	20	IDLE
47	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:09:26	0000-00-00 00:00:00	20	IDLE
48	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:09:46	2022-11-06 17:09:46	20	IDLE
49	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:09:46	0000-00-00 00:00:00	20	IDLE
50	AKT1003SMG                                                                                          	0.25	0.98	2022-11-06 17:10:06	2022-11-06 17:10:06	20	IDLE
51	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:10:11	0000-00-00 00:00:00	20	IDLE
52	AKT1002SMG                                                                                          	0.25	0.98	2022-11-06 17:10:31	2022-11-06 17:10:31	20	IDLE
53	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:10:31	0000-00-00 00:00:00	20	IDLE
54	AKT1002SMG                                                                                          	0.25	0.25	2022-11-06 17:14:08	0000-00-00 00:00:00	20	IDLE
55	AKT1002SMG                                                                                          	0.99	0.98	2022-11-06 17:14:28	2022-11-06 17:14:28	20	IDLE
56	AKT1003SMG                                                                                          	0.25	0.25	2022-11-06 17:14:28	0000-00-00 00:00:00	20	IDLE
57	AKT1003SMG                                                                                          	0.99	0.98	2022-11-06 17:14:48	2022-11-06 17:14:48	20	IDLE
58	AKT1002SMG                                                                                          	0.99	0.99	2022-11-06 17:14:53	0000-00-00 00:00:00	20	IDLE
59	AKT1002SMG                                                                                          	0.99	0.98	2022-11-06 17:15:13	2022-11-06 17:15:13	20	IDLE
60	AKT1003SMG                                                                                          	0.99	0.99	2022-11-06 17:15:13	0000-00-00 00:00:00	20	IDLE
61	AKT1003SMG                                                                                          	0.99	0.98	2022-11-06 17:15:33	2022-11-06 17:15:33	20	IDLE
62	AKT1002SMG                                                                                          	0.99	0.99	2022-11-06 17:15:38	0000-00-00 00:00:00	20	IDLE
63	AKT1002SMG                                                                                          	0.99	0.98	2022-11-06 17:15:58	2022-11-06 17:15:58	20	IDLE
64	AKT1003SMG                                                                                          	0.99	0.99	2022-11-06 17:15:58	0000-00-00 00:00:00	20	IDLE
65	AKT1003SMG                                                                                          	0.99	0.98	2022-11-06 17:16:18	2022-11-06 17:16:18	20	IDLE
66	AKT1002SMG                                                                                          	0.99	0.99	2022-11-06 17:16:23	0000-00-00 00:00:00	20	IDLE
67	AKT1002SMG                                                                                          	0.99	0.98	2022-11-06 17:16:43	2022-11-06 17:16:43	20	IDLE
68	AKT1003SMG                                                                                          	0.99	0.99	2022-11-06 17:16:43	0000-00-00 00:00:00	20	IDLE
69	AKT1003SMG                                                                                          	0.99	0.98	2022-11-06 17:17:03	2022-11-06 17:17:03	20	IDLE
70	AKT1002SMG                                                                                          	0.99	0.99	2022-11-06 17:17:08	0000-00-00 00:00:00	20	IDLE
71	AKT1002SMG                                                                                          	0.99	0.98	2022-11-06 17:17:28	2022-11-06 17:17:28	20	IDLE
72	AKT1003SMG                                                                                          	0.99	0.99	2022-11-06 17:17:28	0000-00-00 00:00:00	20	IDLE
73	AKT1003SMG                                                                                          	0.99	0.98	2022-11-06 17:17:48	2022-11-06 17:17:48	20	IDLE
74	AKT1002SMG                                                                                          	0.99	0.99	2022-11-06 17:17:53	0000-00-00 00:00:00	20	IDLE
75	AKT1002SMG                                                                                          	0.99	0.98	2022-11-06 17:18:13	2022-11-06 17:18:13	20	IDLE
76	AKT1003SMG                                                                                          	0.99	0.99	2022-11-06 17:18:13	0000-00-00 00:00:00	20	IDLE
77	AKT1003SMG                                                                                          	0.99	0.98	2022-11-06 17:18:33	2022-11-06 17:18:33	20	IDLE
78	AKT1002SMG                                                                                          	0.99	0.99	2022-11-06 17:18:38	0000-00-00 00:00:00	20	IDLE
\.


--
-- Data for Name: drone_process_hist; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.drone_process_hist (hist_item_id, drone_serial_number, drone_model, drone_weight_limit, drone_battery_level, drone_state, med_code, med_name, med_weight, med_image, process_start_time, process_end_time) FROM stdin;
1	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 09:51:16	0000-00-00 00:00:00
2	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 09:53:38	0000-00-00 00:00:00
3	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 09:56:26	0000-00-00 00:00:00
4	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 10:01:44	0000-00-00 00:00:00
5	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 10:15:17	0000-00-00 00:00:00
6	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 10:20:31	0000-00-00 00:00:00
7	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 14:29:55	0000-00-00 00:00:00
8	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 14:41:31	0000-00-00 00:00:00
9	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 14:45:34	0000-00-00 00:00:00
10	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 14:52:34	0000-00-00 00:00:00
11	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 15:14:24	0000-00-00 00:00:00
12	AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 15:37:37	0000-00-00 00:00:00
13	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADING	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 16:01:43	0000-00-00 00:00:00
14	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADED	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 16:05:45	0000-00-00 00:00:00
15	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADED	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 16:20:33	0000-00-00 00:00:00
16	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADED	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 16:22:19	0000-00-00 00:00:00
17	AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADED	GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png	2022-11-06 16:22:49	0000-00-00 00:00:00
\.


--
-- Data for Name: drone_process_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.drone_process_record (drone_serial_number, drone_model, drone_weight_limit, drone_battery_level, drone_state) FROM stdin;
AKT1005SMG                                                                                          	CruiserWeight  	260	0.16	IDLE
AKT1004SMG                                                                                          	HeavyWeight    	403	0.23	IDLE
AKT1000SMG                                                                                          	LightWeight    	124	0.98	LOADING
AKT1001SMG                                                                                          	MiddleWeight   	220	0.98	LOADED
AKT1006SMG                                                                                          	MiddleWeight   	230	0.21	IDLE
AKT1002SMG                                                                                          	CruiserWeight  	340	0.99	IDLE
AKT1003SMG                                                                                          	HeavyWeight    	480	0.99	IDLE
\.


--
-- Data for Name: medication_tbl; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.medication_tbl (med_code, med_name, med_weight, med_image) FROM stdin;
KBHGY2345_RT    	Antibotics      	300	eteteyrrio.png
WE455-567-09    	Amoxiallin      	100	hdhdjsjdsj.jpg
GDT6688IOP-0    	Cafalexin       	250	64fr6eee6efd.png
GDG4467TRT      	Edoxaban        	480	64trtrtrttr.jpg
\.


--
-- Name: drone_battery_battery_charge_log_hist_item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.drone_battery_battery_charge_log_hist_item_id_seq', 78, true);


--
-- Name: drone_process_hist_hist_item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.drone_process_hist_hist_item_id_seq', 17, true);


--
-- Name: drone_battery_battery_charge_log drone_battery_battery_charge_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.drone_battery_battery_charge_log
    ADD CONSTRAINT drone_battery_battery_charge_log_pkey PRIMARY KEY (hist_item_id);


--
-- Name: drone_process_hist drone_process_hist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.drone_process_hist
    ADD CONSTRAINT drone_process_hist_pkey PRIMARY KEY (hist_item_id);


--
-- Name: drone_process_record drone_process_record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.drone_process_record
    ADD CONSTRAINT drone_process_record_pkey PRIMARY KEY (drone_serial_number);


--
-- PostgreSQL database dump complete
--

