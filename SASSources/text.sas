/* ----------------------------------------
Code exported from SAS Enterprise Guide
DATE: jueves, 28 de febrero de 2019     TIME: 10:41:05
PROJECT: Proyecto_corep_BS_mas_TSB_3281_v2
PROJECT PATH: C:\Users\miquel.mirat\Desktop\testing\proyectos_input\Proyecto_corep_BS_mas_TSB_3281_v2.egp
---------------------------------------- */


/*   START OF NODE: TSB de COREP a Columna SAS   */

PROC SQL; 
 CREATE TABLE tipos_cambio 
as select  *,
'3281' AS CAT
                from oraggcom.canvi_oficial AS AUXILIAR
                               where data_cre='31dec2018:0:0:0'dt and DIVISA='GBP';
quit;


PROC SORT DATA=bsco_4.PLANTILLA_3281 OUT=TEST_3281 (KEEP=TEMPLATE DATA_EXTRA ROW C:);
BY  TEMPLATE DATA_EXTRA ROW;
RUN;

PROC SQL;
 CREATE TABLE WORK.TEST2_3281 AS SELECT *,
 C010*C110*100 AS C_PDxEAD,
  C230*(C110-C140)*100 AS C_LGDxEAD,
   C240*C140*100 AS C_LGDEFxEAD,
    C250*C110 AS C_MxEAD
	/*C110-C140 AS C_EAD_NO_EF*/
	
 FROM WORK.TEST_3281 AS AUXILIAR

 ;
QUIT;


PROC TRANSPOSE DATA=TEST2_3281 OUT=TEST3_3281(RENAME=(_NAME_=COL));
BY TEMPLATE DATA_EXTRA ROW;
VAR C:;
RUN;


PROC SQL;
 CREATE TABLE WORK.CAMBIO_MILES AS SELECT
AUXILIAR.template,
AUXILIAR.data_extra,
AUXILIAR.row,
AUXILIAR.COL,
AUXILIAR.COL1 AS IMPORTE,
AUXILIAR2.CANV_FIT*1000 AS TIPO_CAMBIO

 FROM WORK.TEST3_3281 AS AUXILIAR
 LEFT JOIN WORK.TIPOS_CAMBIO AUXILIAR2 ON (AUXILIAR.template = AUXILIAR2.CAT);

QUIT;


PROC SQL;
 CREATE TABLE WORK.CAMBIO_3281 AS SELECT
AUXILIAR.template,
AUXILIAR.data_extra,
AUXILIAR.row,
AUXILIAR.COL,
AUXILIAR.IMPORTE,
case when COL IN ('C010','C230','C240','C250','C300') then IMPORTE else IMPORTE/TIPO_CAMBIO end AS IMPORTE_mEUR

 FROM WORK.CAMBIO_MILES AS AUXILIAR
 WHERE AUXILIAR.IMPORTE NOT = 0
 
;
QUIT;

/* Ponemos los mismos nombres que en la tabla de BS */

PROC SQL;
   CREATE TABLE TABLA_3281_TSB AS 
   SELECT t1.template AS COREP, 
          t1.data_extra AS CATEGO,
		  t1.ROW,
          t1.COL,
		  t1.IMPORTE_mEUR AS IMPORTE
          FROM WORK.CAMBIO_3281 t1
		  		 
          ;
QUIT;



PROC SQL;
   CREATE TABLE TABLA_3281_VUELTA_TSB AS 
   SELECT t1.COREP AS template, 
          t1.CATEGO AS data_extra,
		  t1.ROW,
          t1.COL,
		  t1.IMPORTE AS COL1
          FROM WORK.TABLA_3281_TSB  t1
		 
          ;
QUIT;


PROC SORT DATA=TABLA_3281_VUELTA_TSB OUT=TEST1_TABLA3281_VUELTA_TSB (KEEP=TEMPLATE DATA_EXTRA ROW C:);
BY  TEMPLATE DATA_EXTRA ROW;
RUN;



PROC TRANSPOSE DATA=TEST1_TABLA3281_VUELTA_TSB OUT=TEST2_TABLA3281_VUELTA_TSB;
BY TEMPLATE DATA_EXTRA ROW;
VAR COL1;
ID COL;
RUN;





/*   START OF NODE: BS de ECO a SAS   */

%let mes=1812;

options compress=binary;

/*EL INPUT ES UN ARCHIVO ECO COREP*/

PROC SQL;
   CREATE TABLE WORK.ECO1 AS 
   SELECT /* C�lculo */
            (case when SUBSTR (F1,1,4) = '3207' then '3207'
            when SUBSTR (F1,1,4) = '3281' then '3281'
            when SUBSTR (F1,1,4) = '3282' then '3282'
            when SUBSTR (F1,1,4) = '3291' then '3291'
            when SUBSTR (F1,1,4) = '3292' then '3292'
            when SUBSTR (F1,1,4) = '3294' then '3294'
            else 'ERRO' end) AS 'COREP'n, 
          t1.F1
      FROM bsco_4.ECO_CP_CONS t1;
QUIT;


PROC SQL;
   CREATE TABLE ECO2 AS 
   SELECT t1.COREP, 
          t1.F1, 
          /* CATEGO */
            (case when COREP = '3207' then SUBSTR (F1,5,4)
            when COREP IN ('3281','3282') then SUBSTR (F1,5,3)
            when COREP IN ('3291','3292','3294') then SUBSTR (F1,5,2)
            else '' end) FORMAT=$CHAR4. AS CATEGO, 
          /* CELDA */
            (case when COREP = '3207' then SUBSTR (F1,125,4)
            when COREP IN ('3281') then SUBSTR (F1,125,4)
            when COREP IN ('3291','3292','3294') then SUBSTR (F1,125,4)
            else '' end) FORMAT=$CHAR4. AS CELDA, 
          /* IMPORTE */ 
			input(case when COREP = '3207' then TRANWRD(SUBSTR (F1,129,18),",",".")  
            when COREP IN ('3281','3282') then TRANWRD(SUBSTR(F1,129,18),",",".")
            when COREP IN ('3291','3292','3294') then TRANWRD(SUBSTR (F1,129,18),",",".")
            else '' end,BEST18.)*(case when SUBSTR (F1,147,1) = '-' then -1 else 1 end)  AS IMPORTE,
		  /* SIGNO */
			(case when SUBSTR (F1,147,1) = '-' then -1 else 1 end) AS SIGNO,
		  /* COLUMNA */
            (case when COREP IN ('3282') then 'C'||SUBSTR (F1,126,3)
            else '' end) FORMAT=$CHAR12. AS COL, 
          /* TRAMO_PD */
            (case when COREP IN ('3282') then input(SUBSTR (F1,45,4),comma32.)
            else 0 end) AS TRAMO_PD
      FROM ECO1 t1;
QUIT;


PROC SQL;
   CREATE TABLE WORK.COR3281_1 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          case when t1.IMPORTE= . then 0 else t1.IMPORTE end AS IMPORTE, 
          t1.COL, 
          t1.TRAMO_PD
      FROM WORK.ECO2 t1
      WHERE t1.COREP = '3281';
QUIT;


PROC SQL;
   CREATE TABLE WORK.COR3281_2 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t2.COREP_2, 
          t2.ROW, 
          t2.COL
      FROM WORK.COR3281_1 t1
           LEFT JOIN BSCO.MAESTRO_CELDAS_COREP t2 ON (t1.COREP = t2.COREP) AND (t1.CELDA = t2.CEL)
      ORDER BY t1.CATEGO,
               t2.COL,
               t2.ROW;
QUIT;



PROC SQL;
   CREATE TABLE WORK.COR3281_3 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t1.COREP_2, 
          t1.ROW, 
          t1.COL
      FROM WORK.COR3281_2 t1
      WHERE t1.COREP = '3281' AND t1.COL IN ('C110');
QUIT;

PROC SQL;
   CREATE TABLE WORK.COR3281_4 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t1.COREP_2, 
          t1.ROW, 
          t1.COL
      FROM WORK.COR3281_2 t1
      WHERE t1.COREP = '3281' AND t1.COL IN ('C140');
QUIT;


PROC SQL;
   CREATE TABLE WORK.CRUCE3281_1 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t1.COREP_2, 
          t1.ROW, 
          t1.COL, 
          t2.IMPORTE AS EAD
      FROM WORK.COR3281_2 t1
           LEFT JOIN WORK.COR3281_3 t2 ON (t1.CATEGO = t2.CATEGO) AND (t1.ROW = t2.ROW);
QUIT;

PROC SQL;
   CREATE TABLE WORK.CRUCE3281_2 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t1.COREP_2, 
          t1.ROW, 
          t1.COL,
          t1.EAD, 
          t2.IMPORTE AS EAD_EEFF
      FROM WORK.CRUCE3281_1 t1
           LEFT JOIN WORK.COR3281_4 t2 ON (t1.CATEGO = t2.CATEGO) AND (t1.ROW = t2.ROW);
QUIT;

PROC SQL;
   CREATE TABLE WORK.CRUCE3281_2_5 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t1.COREP_2, 
          t1.ROW, 
          t1.COL,
          case when t1.EAD = . then 0 else t1.EAD end AS EAD, 
          case when t1.EAD_EEFF = . then 0 else t1.EAD_EEFF end AS EAD_EEFF
      FROM WORK.CRUCE3281_2 t1
           ;
QUIT;


PROC SQL;
   CREATE TABLE WORK.CRUCE3281_3 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          t1.IMPORTE, 
          t1.COREP_2, 
          t1.ROW, 
          t1.COL, 
          t1.EAD,
          t1.EAD_EEFF,
case when COL ='C010' then IMPORTE*EAD/10000 else 0 end AS C_PDxEAD,
case when COL ='C230' then IMPORTE*(EAD-EAD_EEFF)/100 else 0 end AS C_LGDxEAD,
case when COL ='C240' then IMPORTE*EAD_EEFF/100 else 0 end AS C_LGDEFxEAD,
case when COL ='C250' then IMPORTE*EAD else 0 end AS C_MxEAD
  
      FROM WORK.CRUCE3281_2_5 t1
          ;
QUIT;


PROC SQL;
   CREATE TABLE WORK.PDxEAD_3281 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          C_PDxEAD AS IMPORTE, 
		  t1.COREP_2, 
          t1.ROW, 
          'C_PDxEAD' AS COL

          FROM WORK.CRUCE3281_3 t1
		  WHERE C_PDxEAD NOT = 0
          ;
QUIT;

PROC SQL;
   CREATE TABLE WORK.LGDxEAD_3281 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          C_LGDxEAD AS IMPORTE,
		  t1.COREP_2, 
          t1.ROW,  
          'C_LGDxEAD' AS COL

          FROM WORK.CRUCE3281_3 t1
		  WHERE C_LGDxEAD NOT = 0
          ;
QUIT;

PROC SQL;
   CREATE TABLE WORK.LGDEFxEAD_3281 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA, 
          C_LGDEFxEAD AS IMPORTE,
		  t1.COREP_2, 
          t1.ROW,  
          'C_LGDEFxEAD' AS COL

          FROM WORK.CRUCE3281_3 t1
		  WHERE C_LGDEFxEAD NOT = 0
          ;
QUIT;

PROC SQL;
   CREATE TABLE WORK.MxEAD_3281 AS 
   SELECT t1.COREP, 
          t1.F1, 
          t1.CATEGO, 
          t1.CELDA,
		  t1.COREP_2, 
          t1.ROW,  
          C_MxEAD AS IMPORTE, 
          'C_MxEAD' AS COL 
          
          FROM WORK.CRUCE3281_3 t1
		  WHERE C_MxEAD NOT = 0
          ;
QUIT;


data TABLA_3281_BS;
  set WORK.COR3281_2 WORK.PDxEAD_3281 WORK.LGDxEAD_3281 WORK.LGDEFxEAD_3281 WORK.MxEAD_3281;
run;


PROC SQL;
   CREATE TABLE TABLA_3281_VUELTA_BS AS 
   SELECT t1.COREP AS template, 
          t1.CATEGO AS data_extra,
		  t1.ROW,
          t1.COL,
		  t1.COREP_2,
		  t1.CELDA,
		  t1.IMPORTE AS COL1
          FROM WORK.TABLA_3281_BS  t1
		 
          ;
QUIT;

/*Esta parte del c�digo es para pasar de columna a tabla */

PROC SORT DATA=TABLA_3281_VUELTA_BS OUT=TEST1_TABLA3281_VUELTA_BS (KEEP=TEMPLATE COREP_2 DATA_EXTRA ROW CELDA C:);
BY  TEMPLATE COREP_2 DATA_EXTRA ROW  CELDA;
RUN;


PROC TRANSPOSE DATA=TEST1_TABLA3281_VUELTA_BS OUT=TEST2_TABLA3281_VUELTA_BS;
BY TEMPLATE COREP_2 DATA_EXTRA ROW;
VAR COL1;
ID COL;
RUN;


/* Hacemos un check de total BS por categor�as */

PROC SQL;
   CREATE TABLE BSCO_4.CHECK_3281_BS_CONSO AS 
   SELECT t1.template, 
          t1.data_extra, 
		  t1.ROW,
          /* C010 */
            (SUM(t1.C_PDxEAD)/SUM(t1.C110)) AS C010, 
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
            (SUM(t1.C_LGDxEAD)/(SUM(t1.C110)-SUM(t1.C140))) AS C230, 
          /* C240 */
            (SUM(t1.C_LGDEFxEAD)/SUM(t1.C140)) AS C240, 
          /* C250 */
            (SUM(t1.C_MxEAD)/SUM(t1.C110)) AS C250, 
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
            (SUM(t1.C_MxEAD)) AS C_MxEAD
		
      FROM WORK.TEST2_TABLA3281_VUELTA_BS t1
	  WHERE t1.ROW = 'R010'
      GROUP BY t1.template,
               t1.data_extra,
			   t1.ROW;
QUIT;

/* Finalmente, unimos BS y TSB y sumamos. Lo convertiremos en tabla */


data TABLA3281_BS_TSB_PRE1;
  set WORK.TEST2_TABLA3281_VUELTA_BS WORK.TEST2_TABLA3281_VUELTA_TSB;
run;

/* Convierto los puntos en 0 */

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
		/* EADs de ponderaci�n */
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
C_MxEAD/C110_M AS C250, /* ATENCI�N PROBLEMA!!!: aqu� intent� meter un ROUND, pero no funciona: ROUND((C_MxEAD/C110_M),0) AS C250 */
t1.C255,
t1.C260,
t1.C270,
t1.C280,  
t1.C290,
t1.C300 
          		
      FROM WORK.TABLA3281_BS_TSB_PRE2 t1;
QUIT;

/* Hacemos un check de totales de BS+TSB */

PROC SQL;
   CREATE TABLE bsco_4.CHECK_3281_BS_TSB_CONSO AS 
   SELECT *
          		
      FROM WORK.TABLA3281_BS_TSB_FIN t1
WHERE t1.ROW = 'R010';
QUIT;

/* Ordenamos los datos para convertirlos en columna */

PROC SORT DATA=WORK.TABLA3281_BS_TSB_FIN OUT=ORDEN_FIN3281 (KEEP=TEMPLATE DATA_EXTRA ROW C:);
BY  TEMPLATE DATA_EXTRA ROW;
RUN;


/* Convertimos el fichero en una columna */

PROC TRANSPOSE DATA=ORDEN_FIN3281 OUT=COL_FIN3281 (RENAME=(_NAME_=COL));
BY TEMPLATE DATA_EXTRA ROW;
VAR C:;
RUN;

/* Y lo enlazamos una vez con MAESTRO_CELDAS para obtener la informaci�n de celdas */

PROC SQL;
   CREATE TABLE WORK.PYRAMID_3281_BS_TSB_FIN AS 
   SELECT t1.TEMPLATE, 
          t1.ROW,
		  t1.COL,
          t2.COREP_2, 
		  t1.DATA_EXTRA, 
		  t2.CEL,
		  t1.COL1
      FROM WORK.COL_FIN3281 t1
	  LEFT JOIN BSCO.MAESTRO_CELDAS_COREP t2 ON (t1.TEMPLATE = t2.COREP) AND (t1.ROW = t2.ROW) AND (t1.COL = t2.COL)
	  WHERE t1.COL1 NOT = .
	  ORDER BY t2.COREP_2,
               t1.DATA_EXTRA,
               t2.CEL
	 ;
QUIT;

DATA bsco_4.PYRAMID_3281_FIN_CONSO_&mes.;
SET WORK.PYRAMID_3281_BS_TSB_FIN;
RUN;

GOPTIONS NOACCESSIBLE;
%LET _CLIENTTASKLABEL=;
%LET _CLIENTPROCESSFLOWNAME=;
%LET _CLIENTPROJECTPATH=;
%LET _CLIENTPROJECTPATHHOST=;
%LET _CLIENTPROJECTNAME=;
%LET _SASPROGRAMFILE=;
%LET _SASPROGRAMFILEHOST=;


/*   START OF NODE: TABLA_C92_X1_PD_LGD   */
%LET _CLIENTTASKLABEL='TABLA_C92_X1_PD_LGD';
%LET _CLIENTPROCESSFLOWNAME='Flujo del proceso';
%LET _CLIENTPROJECTPATH='C:\Users\miquel.mirat\Desktop\testing\proyectos_input\Proyecto_corep_BS_mas_TSB_3281_v2.egp';
%LET _CLIENTPROJECTPATHHOST='ITSN149';
%LET _CLIENTPROJECTNAME='Proyecto_corep_BS_mas_TSB_3281_v2.egp';
%LET _SASPROGRAMFILE='';
%LET _SASPROGRAMFILEHOST='';

GOPTIONS ACCESSIBLE;
/* 0) Filtramos y agrupamos a partir del Total Check las casillas de PD y LGD para el C.9.2 */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO AS 
   SELECT t1.template, 
          t1.data_extra, 
          t1.ROW, 
          /* PDxEAD */
            (t1.C010*t1.C110) AS PDxEAD, 
          t1.C110 AS C110_EAD, 
          /* LGDxEAD_noEEFF */
            (t1.C230*(t1.C110-t1.C140)) AS LGDxEAD_noEEFF, 
          t1.C140 AS C140_EAD_EEFF, 
          /* EAD_no_EEFF */
            (t1.C110-t1.C140) AS EAD_no_EEFF, 
          /* CAT_TOTAL */
            (case when data_extra IN ('006','007','011','013','014','015','016','017') then 1 else 0 end) AS CAT_TOTAL, 
          /* CAT_CORP */
            (case when data_extra IN ('007','011') then 1 else 0 end) AS CAT_CORP, 
          /* CAT_RETAIL */
            (case when data_extra IN ('013','014','015','016','017') then 1 else 0 end) AS CAT_RETAIL, 
          /* CAT_HIPO */
            (case when data_extra IN ('013','014') Then 1 else 0 end) AS CAT_HIPO, 
          /* CAT_OTHER_RETAIL */
            (case when data_extra IN ('016','017') then 1 else 0 end) AS CAT_OTHER_RETAIL
      FROM BSCO_4.CHECK_3281_BS_TSB_CONSO t1;
QUIT;

/* 1)Agrupamos y sumamos para la categor�a corporate */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO_CORP AS 
   SELECT (SUM(t1.PDxEAD)) AS PDxEAD, 
          (SUM(t1.C110_EAD)) AS C110_EAD, 
          (SUM(t1.LGDxEAD_noEEFF)) AS LGDxEAD_noEEFF, 
		  (SUM(t1.C140_EAD_EEFF)) AS C140_EAD_EEFF, 
		  (SUM(t1.EAD_no_EEFF)) AS EAD_no_EEFF,
		  '0703' AS CEL_PD,
 		  '0803' AS CEL_LGD,
          'R030' AS ROW
      FROM WORK.C92_1_CATEGO t1
      WHERE t1.CAT_CORP = 1
      GROUP BY t1.CAT_CORP;
QUIT;

/* 2)Agrupamos y sumamos para la categor�a retail */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO_RETAIL AS 
   SELECT (SUM(t1.PDxEAD)) AS PDxEAD, 
          (SUM(t1.C110_EAD)) AS C110_EAD, 
          (SUM(t1.LGDxEAD_noEEFF)) AS LGDxEAD_noEEFF, 
		  (SUM(t1.C140_EAD_EEFF)) AS C140_EAD_EEFF, 
		  (SUM(t1.EAD_no_EEFF)) AS EAD_no_EEFF, 
          '0706' AS CEL_PD,
 		  '0806' AS CEL_LGD,
		  'R060' AS ROW
      FROM WORK.C92_1_CATEGO t1
      WHERE t1.CAT_RETAIL = 1
      GROUP BY t1.CAT_RETAIL;
QUIT;

/* 3)Agrupamos y sumamos para la categor�a hipotecaria */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO_HIPO AS 
   SELECT (SUM(t1.PDxEAD)) AS PDxEAD, 
          (SUM(t1.C110_EAD)) AS C110_EAD, 
          (SUM(t1.LGDxEAD_noEEFF)) AS LGDxEAD_noEEFF, 
		  (SUM(t1.C140_EAD_EEFF)) AS C140_EAD_EEFF, 
		  (SUM(t1.EAD_no_EEFF)) AS EAD_no_EEFF,
	      '0707' AS CEL_PD,
 		  '0807' AS CEL_LGD, 
          'R070' AS ROW
      FROM WORK.C92_1_CATEGO t1
      WHERE t1.CAT_HIPO = 1
      GROUP BY t1.CAT_HIPO;
QUIT;

/* 4)Agrupamos y sumamos para la categor�a hipotecaria */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO_OTHER_RETAIL AS 
   SELECT (SUM(t1.PDxEAD)) AS PDxEAD, 
          (SUM(t1.C110_EAD)) AS C110_EAD, 
          (SUM(t1.LGDxEAD_noEEFF)) AS LGDxEAD_noEEFF, 
		  (SUM(t1.C140_EAD_EEFF)) AS C140_EAD_EEFF, 
		  (SUM(t1.EAD_no_EEFF)) AS EAD_no_EEFF, 
          '0711' AS CEL_PD,
 		  '0811' AS CEL_LGD,
		  'R110' AS ROW
      FROM WORK.C92_1_CATEGO t1
      WHERE t1.CAT_OTHER_RETAIL = 1
      GROUP BY t1.CAT_OTHER_RETAIL;
QUIT;

/* 5)Agrupamos y sumamos para la categor�a total */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO_TOTAL AS 
   SELECT
   (SUM(t1.PDxEAD)) AS PDxEAD, 
    (SUM(t1.C110_EAD)) AS C110_EAD, 
          (SUM(t1.LGDxEAD_noEEFF)) AS LGDxEAD_noEEFF, 
		  (SUM(t1.C140_EAD_EEFF)) AS C140_EAD_EEFF, 
		  (SUM(t1.EAD_no_EEFF)) AS EAD_no_EEFF, 
          '0715' AS CEL_PD,
 		  '0815' AS CEL_LGD,
		  'R150' AS ROW
      FROM WORK.C92_1_CATEGO t1
      WHERE t1.CAT_TOTAL = 1
      GROUP BY t1.CAT_TOTAL;
QUIT;

/* 6) Filtramos el resto de categor�as necesarias */

PROC SQL;
   CREATE TABLE WORK.C92_1_CATEGO_RESTO AS 
   SELECT t1.PDxEAD, 
          t1.C110_EAD, 
          t1.LGDxEAD_noEEFF, 
          t1.C140_EAD_EEFF, 
          t1.EAD_no_EEFF, 
          /* CEL_PD */
            (case when DATA_EXTRA ='006' then '0702'
            when DATA_EXTRA ='007' then '0705'
            when DATA_EXTRA ='013' then '0708'
            when DATA_EXTRA ='014' then '0709'
            when DATA_EXTRA ='015' then '0710'
            when DATA_EXTRA ='016' then '0712'
            when DATA_EXTRA ='017' then '0713' else '' end
            ) AS CEL_PD, 
          /* CEL_LGD */
            (case when DATA_EXTRA ='006' then '0802'
            when DATA_EXTRA ='007' then '0805'
            when DATA_EXTRA ='013' then '0808'
            when DATA_EXTRA ='014' then '0809'
            when DATA_EXTRA ='015' then '0810'
            when DATA_EXTRA ='016' then '0812'
            when DATA_EXTRA ='017' then '0813' else '' end
            ) AS CEL_LGD, 
          /* ROW */
            (case when DATA_EXTRA ='006' then 'R020'
            when DATA_EXTRA ='007' then 'R050'
            when DATA_EXTRA ='013' then 'R080'
            when DATA_EXTRA ='014' then 'R090'
            when DATA_EXTRA ='015' then 'R100'
            when DATA_EXTRA ='016' then 'R120'
            when DATA_EXTRA ='017' then 'R130' else '' end) AS ROW
      FROM WORK.C92_1_CATEGO t1;
QUIT;


/* 7) Agrupamos todas las tablas */

data C92_PD_LGD_1;
  set WORK.C92_1_CATEGO_RESTO WORK.C92_1_CATEGO_TOTAL WORK.C92_1_CATEGO_CORP WORK.C92_1_CATEGO_RETAIL WORK.C92_1_CATEGO_HIPO WORK.C92_1_CATEGO_OTHER_RETAIL;
run;


PROC SORT DATA=WORK.C92_PD_LGD_1 OUT=ORDEN_C92 /*(KEEP=TEMPLATE DATA_EXTRA ROW C:)*/;
BY ROW;
RUN;

/* 8) Calculamos los valores de PD y LGD */

PROC SQL;
   CREATE TABLE BSCO_4.C92_PD_LGD_2_CONSO AS 
   SELECT t1.CEL_PD, 
          t1.CEL_LGD, 
          t1.ROW,
		  'X1' as data_extra, 
          /* PD */
            (PDxEAD/C110_EAD) AS PD, 
          /* LGD */
            (t1.LGDxEAD_noEEFF/t1.EAD_no_EEFF) AS LGD
      FROM WORK.ORDEN_C92 t1
      WHERE t1.CEL_PD NOT = '';
QUIT;

GOPTIONS NOACCESSIBLE;
%LET _CLIENTTASKLABEL=;
%LET _CLIENTPROCESSFLOWNAME=;
%LET _CLIENTPROJECTPATH=;
%LET _CLIENTPROJECTPATHHOST=;
%LET _CLIENTPROJECTNAME=;
%LET _SASPROGRAMFILE=;
%LET _SASPROGRAMFILEHOST=;

