###############################
# data analytics lab load script to read script
# imports csv stock quotes from finance.yahoo.com
###############################

create database data_analytics_2017;

use data_analytics_2017;

set global local_infile=1;

# ï»¿Date	Open	High	Low	Close	Volume
drop table IF EXISTS spy;
create table spy(
date date,	 
open float,	
high float,	
low float,
close float,	
volume float
);

load data local infile "C:/Lab5/LabStockAnalytics/spy_10042017.csv" 
into table spy fields terminated by ',' lines terminated by '\n' ignore 1 lines
(@datevar, @openvar, @highvar, @lowvar, @closevar, volume)
SET date = STR_TO_DATE(@datevar, '%d-%M-%y'),
open = ROUND(@openvar, 2),
high = ROUND(@highvar, 2),  
low = ROUND(@lowvar, 2), 
close = ROUND(@closevar, 2);

show warnings;

select * from spy;

###############################

drop table IF EXISTS goog;
create table goog(
date date,	 
open float,	
high float,	
low float,
close float,	
volume float
);

load data local infile "C:/Lab5/LabStockAnalytics/goog_10042017.csv" 
into table goog fields terminated by ',' lines terminated by '\n' ignore 1 lines
(@datevar, @openvar, @highvar, @lowvar, @closevar, volume)
SET date = STR_TO_DATE(@datevar, '%d-%M-%y'),
open = ROUND(@openvar, 2),
high = ROUND(@highvar, 2),  
low = ROUND(@lowvar, 2), 
close = ROUND(@closevar, 2);

show warnings;

select * from goog;

###############################

drop table if exists celg;
create table celg(
date date,	 
open float,	
high float,	
low float,
close float,	
volume float
);

load data local infile "C:/Lab5/LabStockAnalytics/celg_10042017.csv" 
into table celg fields terminated by ',' lines terminated by '\n' ignore 1 lines
(@datevar, @openvar, @highvar, @lowvar, @closevar, volume)
SET date = STR_TO_DATE(@datevar, '%d-%M-%y'),
open = ROUND(@openvar, 2),
high = ROUND(@highvar, 2),  
low = ROUND(@lowvar, 2), 
close = ROUND(@closevar, 2);

show warnings;

select * from celg;

###############################

drop table IF EXISTS nvda;
create table nvda(
date date,	 
open float,	
high float,	
low float,
close float,	
volume float
);

load data local infile "C:/Lab5/LabStockAnalytics/nvda_10042017.csv" 
into table nvda fields terminated by ',' lines terminated by '\n' ignore 1 lines
(@datevar, @openvar, @highvar, @lowvar, @closevar, volume)
SET date = STR_TO_DATE(@datevar, '%d-%M-%y'),
open = ROUND(@openvar, 2),
high = ROUND(@highvar, 2),  
low = ROUND(@lowvar, 2), 
close = ROUND(@closevar, 2);

show warnings;

select * from nvda;

###############################

drop table IF EXISTS fb;
create table fb(
date date,	 
open float,	
high float,	
low float,
close float,	
volume float
);

load data local infile "C:/Lab5/LabStockAnalytics/fb_10042017.csv" 
into table fb fields terminated by ',' lines terminated by '\n' ignore 1 lines
(@datevar, @openvar, @highvar, @lowvar, @closevar, volume)
SET date = STR_TO_DATE(@datevar, '%d-%M-%y'),
open = ROUND(@openvar, 2),
high = ROUND(@highvar, 2),  
low = ROUND(@lowvar, 2), 
close = ROUND(@closevar, 2);

show warnings;

select * from fb;

######################

drop table IF exists portfolio;
create table portfolio(
	date date,
    nvda_adj_close float, nvda_cum_close float, nvda_value float,
    goog_adj_close float, goog_cum_close float, goog_value float,
    fb_adj_close float, fb_cum_close float, fb_value float,
    celg_adj_close float, celg_cum_close float, celg_value float,
    spy_adj_close float, spy_cum_close float, spy_value float,
    port_cum_return float, port_value float
);

Insert into portfolio(date, nvda_adj_close, nvda_cum_close, nvda_value, goog_adj_close, 
					  goog_cum_close, goog_value, fb_adj_close, fb_cum_close, fb_value,
					  celg_adj_close, celg_cum_close, celg_value, spy_adj_close, 
                      spy_cum_close, spy_value, port_cum_return, port_value)
	select spy.date, nvda.close, NULL, NULL, goog.close, NULL, NULL, fb.close, NULL, NULL,
		   celg.close, NULL, NULL, spy.close, NULL, NULL, NULL, NULL
	from spy, nvda, fb, celg, goog
	where spy.date=nvda.date and spy.date=goog.date and spy.date=fb.date and spy.date=celg.date
    order by spy.date;

select * from portfolio;

select nvda.date, round(nvda.close / first_value(nvda.close) over( order by nvda.date), 2) AS result from nvda;

update portfolio
set nvda_cum_close=(select 
					round(nvda.close / first_value(nvda.close) over( order by nvda.date), 2) 
                    AS result from nvda where portfolio.date = nvda.date)
where portfolio.nvda_cum_close = NULL;


