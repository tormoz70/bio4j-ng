/*#typantq*/
select a.typantq_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.TYPANTQ a
 where a.isdeleted = '0'
 order by a.sord

/*#aqurdthng*/
select a.aqurdthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.AQURDTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#autorthng*/
 select a.autorthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.AUTORTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#storythng*/
select a.storythng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.STORYTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#stylethng*/
select a.stylethng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.STYLETHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#techthng*/
select a.techthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.TECHTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#colorthng*/
select a.colorthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.COLORTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#matrlthng*/
select a.matrlthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.MATRLTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#decorthng*/
select a.decorthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.DECORTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#facingthng*/
select a.facingthng_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.FACINGTHNG a
 where a.isdeleted = '0'
 order by a.sord

/*#crereason*/
select a.crereason_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.CREREASON a
 where a.isdeleted = '0'
 order by a.sord

/*#theftmthd*/
select a.theftmthd_id as id, a.avalue, a.adesc, a.sord
  from IDBM_API.THEFTMTHD a
 where a.isdeleted = '0'
 order by a.sord
