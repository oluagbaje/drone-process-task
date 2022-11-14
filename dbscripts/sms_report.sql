
--drop function insert_sms_message_report;
-- drop table sms_message_report;
create table if not exists sms_message_report(
	message_id text not null primary key,
	country_code char(2) not null,
	created_at timestamp not null default current_timestamp,
	status varchar(20) not null,
	message text not null,
	template_id text not null,
	destination_addr text not null,
	check (status in ('SUBMITTED','DELIVERED','FAILED'))
);
CREATE OR REPLACE FUNCTION public.insert_sms_message_report(
	n_message_id text,
	n_created_at timestamp with time zone,
	n_template_id text,
	n_destination_addr text,
	n_country_code character,
	n_status text,
	n_message text)
    RETURNS void
    LANGUAGE 'sql'    
AS $BODY$
with n_sms_message_report as (
	insert into sms_message_report(message_id, created_at, template_id, country_code, status, message, destination_addr) 
	select  n_message_id, n_created_at, n_template_id, n_country_code, n_status, n_message, n_destination_addr
	where not exists (select * from sms_message_report where message_id = n_message_id )
	returning *
)
update sms_message_report set status = n_status
where not exists (select * from n_sms_message_report) and message_id = n_message_id;
$BODY$;
