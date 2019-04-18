
PROC SQL;
   CREATE TABLE 
   WORK.TABLA3281_BS_TSB_PRE1_5
   AS 
   SELECT 
   t1.template as sss, 
   t1.data_extra,
   t1.ROW,
case when C010 = . then 0 else C010 end AS C010, 
case when C020 = . then 0 else C020 end AS C020,
case when C030 = . then 0 else C030 end AS C030, 
case when C040 = . then 0 else C040 end AS C040, 
case when C070 = . then 0 else C070 end AS C070, 

C_PDxEAD,
C_LGDxEAD,
C_LGDEFxEAD,
C_MxEAD

         
      FROM WORK.TABLA3281_BS_TSB_PRE1 t1
      ;
QUIT;



