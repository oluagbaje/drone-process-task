--
-- PostgreSQL database dump
--

-- Dumped from database version 12.5
-- Dumped by pg_dump version 13.1

-- Started on 2021-08-24 09:11:03

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

--
-- TOC entry 223 (class 1255 OID 16537)
-- Name: get_event_template(text, character, character); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_event_template(r_event_name text, r_country_code character, r_language_code character) RETURNS TABLE(template_id integer, template_content text, default_priority integer)
    LANGUAGE sql
    AS $$
	select template_id, template_content, default_priority 
	from event_templates t
	join events e on e.event_id = t.event_id 
	where e.event_name = r_event_name and e.country_code = r_country_code and t.language_code = r_language_code;
	
$$;


ALTER FUNCTION public.get_event_template(r_event_name text, r_country_code character, r_language_code character) OWNER TO postgres;

--
-- TOC entry 209 (class 1255 OID 16534)
-- Name: update_insert_event(text, text, character, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_insert_event(r_event_id text, r_event_name text, r_country_code character, r_default_priority integer) RETURNS TABLE(event_id text, event_name text, country_code character, default_priority integer)
    LANGUAGE sql
    AS $$
	with uEvent as (
		update events set default_priority = r_default_priority, event_name = r_event_name,  country_code = r_country_code
		where event_id = r_event_id
		returning *
	),
	nEvent as (
		insert into events(event_id, event_name,country_code,default_priority) 
		select r_event_id, r_event_name, r_country_code, r_default_priority 
		where not exists (select event_id from uEvent)
		returning *
	)
	select event_id, event_name,country_code,default_priority from nEvent 
	union
	select event_id, event_name,country_code,default_priority from events where event_id = r_event_id
$$;


ALTER FUNCTION public.update_insert_event(r_event_id text, r_event_name text, r_country_code character, r_default_priority integer) OWNER TO postgres;

--
-- TOC entry 210 (class 1255 OID 16535)
-- Name: update_insert_event_template(text, text, character); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_insert_event_template(r_event_id text, r_template_content text, r_language_code character) RETURNS TABLE(template_id integer, event_id text, language_code character, template_content text)
    LANGUAGE sql
    AS $$
	with dEventTemplate as (
		delete from event_templates 
		where event_id = r_event_id and language_code = r_language_code
	),
	nEventTemplate as (
		insert into event_templates(event_id,language_code,template_content) 
		select r_event_id, r_language_code, r_template_content 		
		returning *
	)
	select * from nEventTemplate 
	
$$;


ALTER FUNCTION public.update_insert_event_template(r_event_id text, r_template_content text, r_language_code character) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 208 (class 1259 OID 16517)
-- Name: event_templates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_templates (
    template_id integer NOT NULL,
    event_id text,
    language_code character(2) NOT NULL,
    template_content text NOT NULL
);


ALTER TABLE public.event_templates OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16515)
-- Name: event_templates_template_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_templates_template_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_templates_template_id_seq OWNER TO postgres;

--
-- TOC entry 2861 (class 0 OID 0)
-- Dependencies: 207
-- Name: event_templates_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_templates_template_id_seq OWNED BY public.event_templates.template_id;


--
-- TOC entry 206 (class 1259 OID 16482)
-- Name: events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.events (
    event_name text NOT NULL,
    country_code character(2) NOT NULL,
    default_priority integer DEFAULT 2 NOT NULL,
    event_id text NOT NULL
);


ALTER TABLE public.events OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16451)
-- Name: smsdeliveryreport; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.smsdeliveryreport (
    deli_id integer NOT NULL,
    messg_id character varying NOT NULL,
    statussent character varying,
    destnaddr character varying NOT NULL,
    country_code character varying NOT NULL,
    sms_message text NOT NULL,
    submittedsent character varying,
    deliveredsent character varying,
    error_code character varying,
    error_message character varying,
    datesent character varying,
    donedatesent character varying
);


ALTER TABLE public.smsdeliveryreport OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 16457)
-- Name: smsdeliveryreport_deli_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.smsdeliveryreport_deli_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.smsdeliveryreport_deli_id_seq OWNER TO postgres;

--
-- TOC entry 2862 (class 0 OID 0)
-- Dependencies: 203
-- Name: smsdeliveryreport_deli_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.smsdeliveryreport_deli_id_seq OWNED BY public.smsdeliveryreport.deli_id;


--
-- TOC entry 204 (class 1259 OID 16459)
-- Name: smstemplate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.smstemplate (
    template_id integer NOT NULL,
    template_name character varying NOT NULL,
    country_code character varying NOT NULL,
    template_sms text NOT NULL,
    temp_count_param integer NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    date_created timestamp without time zone NOT NULL,
    language_code character(2) NOT NULL
);


ALTER TABLE public.smstemplate OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16465)
-- Name: smstemplate_template_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.smstemplate_template_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.smstemplate_template_id_seq OWNER TO postgres;

--
-- TOC entry 2863 (class 0 OID 0)
-- Dependencies: 205
-- Name: smstemplate_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.smstemplate_template_id_seq OWNED BY public.smstemplate.template_id;


--
-- TOC entry 2713 (class 2604 OID 16520)
-- Name: event_templates template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_templates ALTER COLUMN template_id SET DEFAULT nextval('public.event_templates_template_id_seq'::regclass);


--
-- TOC entry 2710 (class 2604 OID 16467)
-- Name: smsdeliveryreport deli_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.smsdeliveryreport ALTER COLUMN deli_id SET DEFAULT nextval('public.smsdeliveryreport_deli_id_seq'::regclass);


--
-- TOC entry 2711 (class 2604 OID 16468)
-- Name: smstemplate template_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.smstemplate ALTER COLUMN template_id SET DEFAULT nextval('public.smstemplate_template_id_seq'::regclass);


--
-- TOC entry 2855 (class 0 OID 16517)
-- Dependencies: 208
-- Data for Name: event_templates; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2853 (class 0 OID 16482)
-- Dependencies: 206
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--




--
-- TOC entry 2849 (class 0 OID 16451)
-- Dependencies: 202
-- Data for Name: smsdeliveryreport; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2851 (class 0 OID 16459)
-- Dependencies: 204
-- Data for Name: smstemplate; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2864 (class 0 OID 0)
-- Dependencies: 207
-- Name: event_templates_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_templates_template_id_seq', 210, true);


--
-- TOC entry 2865 (class 0 OID 0)
-- Dependencies: 203
-- Name: smsdeliveryreport_deli_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.smsdeliveryreport_deli_id_seq', 3, true);


--
-- TOC entry 2866 (class 0 OID 0)
-- Dependencies: 205
-- Name: smstemplate_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.smstemplate_template_id_seq', 1, true);


--
-- TOC entry 2721 (class 2606 OID 16525)
-- Name: event_templates event_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_templates
    ADD CONSTRAINT event_templates_pkey PRIMARY KEY (template_id);


--
-- TOC entry 2719 (class 2606 OID 16514)
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (event_id);


--
-- TOC entry 2715 (class 2606 OID 16470)
-- Name: smsdeliveryreport smsdeliveryreport_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.smsdeliveryreport
    ADD CONSTRAINT smsdeliveryreport_pkey PRIMARY KEY (deli_id);


--
-- TOC entry 2717 (class 2606 OID 16472)
-- Name: smstemplate smstemplate_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.smstemplate
    ADD CONSTRAINT smstemplate_pkey PRIMARY KEY (template_id);


--
-- TOC entry 2722 (class 2606 OID 16526)
-- Name: event_templates event_templates_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_templates
    ADD CONSTRAINT event_templates_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(event_id) ON UPDATE CASCADE ON DELETE CASCADE;


-- Completed on 2021-08-24 09:11:04

--
-- PostgreSQL database dump complete
--

