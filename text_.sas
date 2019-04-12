
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
case when C080 = . then 0 else C080 end AS C080,
case when C090 = . then 0 else C090 end AS C090, 
case when C100 = . then 0 else C100 end AS C100, 
case when C110 = . then 0 else C110 end AS C110, 
case when C120 = . then 0 else C120 end AS C120,
case when C130 = . then 0 else C130 end AS C130,
case when C140 = . then 0 else C140 end AS C140,
case when C150 = . then 0 else C150 end AS C150, 
case when C180 = . then 0 else C180 end AS C180, 
case when C190 = . then 0 else C190 end AS C190, 
case when C200 = . then 0 else C200 end AS C200, 
case when C230 = . then 0 else C230 end AS C230, 
case when C240 = . then 0 else C240 end AS C240, 
case when C250 = . then 0 else C250 end AS C250, 
case when C255 = . then 0 else C255 end AS C255,
case when C250 = . then 0 else C250 end AS C250,
case when C260 = . then 0 else C260 end AS C260,
case when C270 = . then 0 else C270 end AS C270,
case when C280 = . then 0 else C280 end AS C280,
case when C290 = . then 0 else C290 end AS C290,
case when C300 = . then 0 else C300 end AS C300,
C_PDxEAD,
C_LGDxEAD,
C_LGDEFxEAD,
C_MxEAD

         
      FROM WORK.TABLA3281_BS_TSB_PRE1 t1
      ;
QUIT;

PROC SQL;
   CREATE TABLE WORK.TABLA3281_BS_TSB_PRE2 AS 
   SELECT t1.template, 
          t1.data_extra,
		  t1.ROW,	  
          /* C010 */
            (SUM(t1.C010)) AS C010, 
          /* C020 */
            (SUM(t1.C020)) AS C020, 
          /* C030 */
            (SUM(t1.C030)) AS C030, 
          /* C040 */
            (SUM(t1.C040)) AS C040, 
          /* C070 */
            (SUM(t1.C070)) AS C070, 
          /* C080 */
            (SUM(t1.C080)) AS C080, 
          /* C090 */
            (SUM(t1.C090)) AS C090, 
          /* C100 */
            (SUM(t1.C100)) AS C100, 
          /* C110 */
            (SUM(t1.C110)) AS C110, 
          /* C120 */
            (SUM(t1.C120)) AS C120, 
          /* C130 */
            (SUM(t1.C130)) AS C130, 
          /* C140 */
            (SUM(t1.C140)) AS C140, 
          /* C150 */
            (SUM(t1.C150)) AS C150, 
          /* C180 */
            (SUM(t1.C180)) AS C180, 
          /* C190 */
            (SUM(t1.C190)) AS C190, 
          /* C200 */
            (SUM(t1.C200)) AS C200, 
          /* C230 */
            (SUM(t1.C230)) AS C230, 
          /* C240 */
            (SUM(t1.C240)) AS C240, 
          /* C250 */
            (SUM(t1.C250)) AS C250, 
          /* C255 */
            (SUM(t1.C255)) AS C255, 
          /* C260 */
            (SUM(t1.C260)) AS C260, 
          /* C270 */
            (SUM(t1.C270)) AS C270, 
          /* C280 */
            (SUM(t1.C280)) AS C280, 
          /* C290 */
            (SUM(t1.C290)) AS C290, 
          /* C300 */
            (SUM(t1.C300)) AS C300, 
          /* C_PDxEAD */
            (SUM(t1.C_PDxEAD)) AS C_PDxEAD, 
          /* C_LGDxEAD */
            (SUM(t1.C_LGDxEAD)) AS C_LGDxEAD, 
          /* C_LGDEFxEAD */
            (SUM(t1.C_LGDEFxEAD)) AS C_LGDEFxEAD, 
          /* C_MxEAD */
            (SUM(t1.C_MxEAD)) AS C_MxEAD,
		/* EADs de ponderación */
			SUM(case when C_PDxEAD NOT=. then C110 else 0 end) AS C110_PD, 
			SUM(case when C_LGDxEAD NOT=. then C110 else 0 end) AS C110_LGD, 
			SUM(case when C_LGDEFxEAD NOT=. then C140 else 0 end) AS C140_LGD, 
			SUM(case when C_MxEAD NOT=. then C110 else 0 end) AS C110_M
         
      FROM WORK.TABLA3281_BS_TSB_PRE1_5 t1
      GROUP BY t1.template,
               t1.data_extra,
			   t1.ROW;
QUIT;

PROC SQL;
   CREATE TABLE WORK.TABLA3281_BS_TSB_FIN AS 
   SELECT t1.template, 
          t1.data_extra,
		  t1.ROW,	  
t1.C_PDxEAD/C110_PD AS C010, 
t1.C020,
t1.C030, 
t1.C040,
. AS C050,
. AS C060,
t1.C070,
t1.C080,
t1.C090,
t1.C100,
t1.C110,
t1.C120,
t1.C130,
t1.C140,
t1.C150,
. AS C160,
. AS C170,
t1.C180,
t1.C190,
t1.C200,
. AS C210,
. AS C220,
C_LGDxEAD/(C110_LGD-C140_LGD) AS C230,
C_LGDEFxEAD/C140_LGD AS C240,
C_MxEAD/C110_M AS C250, /* ATENCIÓN PROBLEMA!!!: aquí intenté meter un ROUND, pero no funciona: ROUND((C_MxEAD/C110_M),0) AS C250 */
t1.C255,
t1.C260,
t1.C270,
t1.C280,  
t1.C290,
t1.C300 
          		
      FROM WORK.TABLA3281_BS_TSB_PRE2 t1;
QUIT;

/* Hacemos un check de totales de BS+TSB */





